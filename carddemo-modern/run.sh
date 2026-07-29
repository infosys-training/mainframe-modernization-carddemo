#!/usr/bin/env bash
#
# CardDemo Modern — start the backend (FastAPI) and frontend (Vite) locally.
#
# Run ./setup.sh once before using this script. Press Ctrl-C to stop both.
#
#   Backend:  http://localhost:8000   (API docs: http://localhost:8000/docs)
#   Frontend: http://localhost:5173
#   Login:    admin / admin123   (or user0001 / user1234)

set -euo pipefail
cd "$(dirname "$0")"
ROOT="$(pwd)"

DB_URL="${DATABASE_URL:-postgresql://carddemo:carddemo@localhost:5432/carddemo}"

if [ ! -x "$ROOT/backend/.venv/bin/uvicorn" ]; then
  echo "ERROR: backend venv not found. Run ./setup.sh first."
  exit 1
fi

# Ensure PostgreSQL is running (unless a custom DATABASE_URL was supplied).
if [ -z "${DATABASE_URL:-}" ] && command -v docker >/dev/null 2>&1; then
  echo "==> Ensuring PostgreSQL is up (docker compose service 'db')"
  docker compose up -d db
fi

# Start backend.
echo "==> Starting backend on http://localhost:8000"
cd "$ROOT/backend"
DATABASE_URL="$DB_URL" ./.venv/bin/uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload &
BACKEND_PID=$!

# Start frontend.
echo "==> Starting frontend on http://localhost:5173"
cd "$ROOT/frontend"
npm run dev -- --host 0.0.0.0 &
FRONTEND_PID=$!

cleanup() {
  echo ""
  echo "==> Stopping..."
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
}
trap cleanup INT TERM EXIT

echo ""
echo "==> CardDemo Modern is starting:"
echo "    Backend:  http://localhost:8000  (docs: /docs)"
echo "    Frontend: http://localhost:5173"
echo "    Login:    admin / admin123"
echo "    Press Ctrl-C to stop."

# Wait for either process to exit.
wait -n "$BACKEND_PID" "$FRONTEND_PID"
