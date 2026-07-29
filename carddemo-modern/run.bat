@echo off
REM ===========================================================================
REM  CardDemo Modern - start the backend (FastAPI) and frontend (Vite) (Windows).
REM
REM  Run setup.bat once before using this script. Each service opens in its own
REM  window; close those windows to stop them.
REM
REM    Backend:  http://localhost:8000   (API docs: http://localhost:8000/docs)
REM    Frontend: http://localhost:5173
REM    Login:    admin / admin123   (or user0001 / user1234)
REM ===========================================================================
setlocal enableextensions
cd /d "%~dp0"
set "ROOT=%cd%"

if not defined DATABASE_URL set "DATABASE_URL=postgresql://carddemo:carddemo@localhost:5432/carddemo"

if not exist "%ROOT%\backend\.venv\Scripts\uvicorn.exe" (
  echo ERROR: backend venv not found. Run setup.bat first.
  exit /b 1
)

REM Ensure PostgreSQL is running.
where docker >nul 2>&1
if not errorlevel 1 (
  echo ==^> Ensuring PostgreSQL is up ^(docker compose service 'db'^)
  docker compose up -d db
)

echo ==^> Starting backend on http://localhost:8000
start "carddemo-backend" cmd /k "cd /d "%ROOT%\backend" ^&^& set "DATABASE_URL=%DATABASE_URL%" ^&^& .venv\Scripts\uvicorn.exe app.main:app --host 0.0.0.0 --port 8000 --reload"

echo ==^> Starting frontend on http://localhost:5173
start "carddemo-frontend" cmd /k "cd /d "%ROOT%\frontend" ^&^& npm run dev -- --host 0.0.0.0"

echo.
echo ==^> CardDemo Modern is starting in two new windows:
echo     Backend:  http://localhost:8000  ^(docs: /docs^)
echo     Frontend: http://localhost:5173
echo     Login:    admin / admin123
echo     Close those windows to stop the servers.
endlocal
