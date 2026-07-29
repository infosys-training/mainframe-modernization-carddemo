#!/usr/bin/env bash
#
# CardDemo Modern — first-time local setup.
#
# Installs backend (Python) and frontend (Node) dependencies, starts a local
# PostgreSQL, and loads the COBOL seed data. Run this once; afterwards use
# ./run.sh to start the app.
#
# Requirements: python3 (>=3.10), node/npm (>=18), and Docker (for PostgreSQL).
# If you already have PostgreSQL running locally, set DATABASE_URL and the
# script will skip starting the Docker database.

set -euo pipefail
cd "$(dirname "$0")"
ROOT="$(pwd)"

DB_URL="${DATABASE_URL:-postgresql://carddemo:carddemo@localhost:5432/carddemo}"

echo "==> CardDemo Modern — first-time setup"

# ---------------------------------------------------------------------------
# Prerequisites
# ---------------------------------------------------------------------------
command -v python3 >/dev/null 2>&1 || { echo "ERROR: python3 is required"; exit 1; }
command -v npm     >/dev/null 2>&1 || { echo "ERROR: node/npm is required"; exit 1; }

# ---------------------------------------------------------------------------
# PostgreSQL
# ---------------------------------------------------------------------------
if [ -n "${DATABASE_URL:-}" ]; then
  echo "==> Using provided DATABASE_URL, skipping Docker database"
elif command -v docker >/dev/null 2>&1; then
  echo "==> Starting PostgreSQL (docker compose service 'db')"
  docker compose up -d db
else
  echo "!!  Docker not found and DATABASE_URL not set."
  echo "    Start PostgreSQL yourself and re-run with, e.g.:"
  echo "    DATABASE_URL=postgresql://carddemo:carddemo@localhost:5432/carddemo ./setup.sh"
  exit 1
fi

# ---------------------------------------------------------------------------
# Backend: virtualenv + dependencies
# ---------------------------------------------------------------------------
echo "==> Backend: creating virtualenv and installing dependencies"
cd "$ROOT/backend"
python3 -m venv .venv
./.venv/bin/pip install --upgrade pip
./.venv/bin/pip install -r requirements.txt

# ---------------------------------------------------------------------------
# Wait for the database, then load seed data (idempotent)
# ---------------------------------------------------------------------------
echo "==> Waiting for PostgreSQL to accept connections..."
for _ in $(seq 1 60); do
  if ./.venv/bin/python - "$DB_URL" <<'PY' 2>/dev/null
import sys, socket
from urllib.parse import urlparse
u = urlparse(sys.argv[1])
s = socket.socket(); s.settimeout(1)
sys.exit(0 if s.connect_ex((u.hostname or "localhost", u.port or 5432)) == 0 else 1)
PY
  then
    break
  fi
  sleep 1
done

echo "==> Loading seed data (creates tables + loads COBOL ASCII data)"
DATABASE_URL="$DB_URL" ./.venv/bin/python -m app.migrate_data

# ---------------------------------------------------------------------------
# Frontend: npm dependencies
# ---------------------------------------------------------------------------
echo "==> Frontend: installing npm dependencies"
cd "$ROOT/frontend"
npm install

echo ""
echo "==> Setup complete. Start the app with:  ./run.sh"
