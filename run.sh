#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SOLUTION_PATH="$ROOT_DIR/LinkedList.Polynomial.sln"
APP_PROJECT="$ROOT_DIR/src/LinkedList.Polynomial.App/LinkedList.Polynomial.App.csproj"
CONFIGURATION="Release"

log() {
  printf '[run.sh] %s\n' "$1"
}

if ! command -v dotnet >/dev/null 2>&1; then
  printf '[run.sh] ERROR: dotnet is not installed or not in PATH.\n' >&2
  exit 127
fi

DOTNET_VERSION="$(dotnet --version)"
if [[ "$DOTNET_VERSION" != 9.0.* ]]; then
  printf '[run.sh] ERROR: .NET SDK 9.0.x is required. Current: %s\n' "$DOTNET_VERSION" >&2
  exit 2
fi

if [[ ! -f "$SOLUTION_PATH" ]]; then
  printf '[run.sh] ERROR: Solution file not found: %s\n' "$SOLUTION_PATH" >&2
  exit 3
fi

log "dotnet restore"
dotnet restore "$SOLUTION_PATH"

log "dotnet build ($CONFIGURATION)"
dotnet build "$SOLUTION_PATH" --configuration "$CONFIGURATION" --no-restore

log "dotnet test ($CONFIGURATION)"
dotnet test "$SOLUTION_PATH" --configuration "$CONFIGURATION" --no-build

log "dotnet run ($CONFIGURATION)"
dotnet run --project "$APP_PROJECT" --configuration "$CONFIGURATION" --no-build -- "$@"

log "Completed successfully."
