@echo off
REM ===========================================================================
REM  CardDemo Modern - first-time local setup (Windows).
REM
REM  Installs backend (Python) and frontend (Node) dependencies, starts a local
REM  PostgreSQL, and loads the COBOL seed data. Run this once; afterwards use
REM  run.bat to start the app.
REM
REM  Requirements: python (>=3.10), node/npm (>=18), and Docker Desktop.
REM  If you already have PostgreSQL running, set DATABASE_URL before running and
REM  the Docker database step is skipped.
REM ===========================================================================
setlocal enableextensions
cd /d "%~dp0"
set "ROOT=%cd%"

if not defined DATABASE_URL set "DATABASE_URL=postgresql://carddemo:carddemo@localhost:5432/carddemo"

echo ==^> CardDemo Modern - first-time setup

REM --- Prerequisites -------------------------------------------------------
where python >nul 2>&1
if errorlevel 1 (
  echo ERROR: python is required and must be on PATH.
  exit /b 1
)
where npm >nul 2>&1
if errorlevel 1 (
  echo ERROR: node/npm is required and must be on PATH.
  exit /b 1
)

REM --- PostgreSQL ----------------------------------------------------------
if defined DATABASE_URL_PROVIDED (
  echo ==^> Using provided DATABASE_URL, skipping Docker database
) else (
  where docker >nul 2>&1
  if errorlevel 1 (
    echo !!  Docker not found. Start PostgreSQL yourself, set DATABASE_URL, and re-run.
    exit /b 1
  )
  echo ==^> Starting PostgreSQL ^(docker compose service 'db'^)
  docker compose up -d db
  if errorlevel 1 exit /b 1
)

REM --- Backend: virtualenv + dependencies ----------------------------------
echo ==^> Backend: creating virtualenv and installing dependencies
cd /d "%ROOT%\backend"
python -m venv .venv
if errorlevel 1 exit /b 1
call ".venv\Scripts\python.exe" -m pip install --upgrade pip
call ".venv\Scripts\pip.exe" install -r requirements.txt
if errorlevel 1 exit /b 1

REM --- Wait for the database, then load seed data (idempotent) -------------
echo ==^> Waiting for PostgreSQL to accept connections...
for /l %%i in (1,1,60) do (
  ".venv\Scripts\python.exe" -c "import os,sys,socket; from urllib.parse import urlparse; u=urlparse(os.environ['DATABASE_URL']); s=socket.socket(); s.settimeout(1); sys.exit(0 if s.connect_ex((u.hostname or 'localhost', u.port or 5432))==0 else 1)" >nul 2>&1 && goto db_ready
  timeout /t 1 /nobreak >nul
)
:db_ready

echo ==^> Loading seed data ^(creates tables + loads COBOL ASCII data^)
".venv\Scripts\python.exe" -m app.migrate_data
if errorlevel 1 exit /b 1

REM --- Frontend: npm dependencies ------------------------------------------
echo ==^> Frontend: installing npm dependencies
cd /d "%ROOT%\frontend"
call npm install
if errorlevel 1 exit /b 1

echo.
echo ==^> Setup complete. Start the app with:  run.bat
endlocal
