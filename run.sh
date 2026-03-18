#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MAIN_SOURCE_DIR="$ROOT_DIR/src/main/java"
TEST_SOURCE_DIR="$ROOT_DIR/src/test/java"
BUILD_DIR="$ROOT_DIR/build"
MAIN_CLASSES_DIR="$BUILD_DIR/classes/main"
TEST_CLASSES_DIR="$BUILD_DIR/classes/test"
MAIN_CLASS="io.github.parkpawapon.linkedlist.app.Main"
TEST_CLASS="io.github.parkpawapon.linkedlist.test.PolynomialTests"
JAVAC_FLAGS=(--release 17 -encoding UTF-8 -Xlint:all -Werror)

log() {
  printf '[run.sh] %s\n' "$1"
}

if ! command -v java >/dev/null 2>&1; then
  printf '[run.sh] ERROR: java is not installed or not in PATH.\n' >&2
  exit 127
fi

if ! command -v javac >/dev/null 2>&1; then
  printf '[run.sh] ERROR: javac is not installed or not in PATH.\n' >&2
  exit 2
fi

JAVA_VERSION_OUTPUT="$(javac -version 2>&1)"
if [[ ! "$JAVA_VERSION_OUTPUT" =~ ^javac\ 17(\..*)?$ ]]; then
  printf '[run.sh] ERROR: JDK 17 is required. Current: %s\n' "$JAVA_VERSION_OUTPUT" >&2
  exit 3
fi

if [[ ! -d "$MAIN_SOURCE_DIR" ]]; then
  printf '[run.sh] ERROR: Main source directory not found: %s\n' "$MAIN_SOURCE_DIR" >&2
  exit 4
fi

if [[ ! -d "$TEST_SOURCE_DIR" ]]; then
  printf '[run.sh] ERROR: Test source directory not found: %s\n' "$TEST_SOURCE_DIR" >&2
  exit 5
fi

rm -rf "$BUILD_DIR"
mkdir -p "$MAIN_CLASSES_DIR" "$TEST_CLASSES_DIR"

MAIN_SOURCES=()
while IFS= read -r source_file; do
  MAIN_SOURCES+=("$source_file")
done < <(find "$MAIN_SOURCE_DIR" -type f -name '*.java' | sort)

if [[ "${#MAIN_SOURCES[@]}" -eq 0 ]]; then
  printf '[run.sh] ERROR: No main Java sources found.\n' >&2
  exit 6
fi

TEST_SOURCES=()
while IFS= read -r source_file; do
  TEST_SOURCES+=("$source_file")
done < <(find "$TEST_SOURCE_DIR" -type f -name '*.java' | sort)

if [[ "${#TEST_SOURCES[@]}" -eq 0 ]]; then
  printf '[run.sh] ERROR: No test Java sources found.\n' >&2
  exit 7
fi

log "Compiling main sources"
javac "${JAVAC_FLAGS[@]}" -d "$MAIN_CLASSES_DIR" "${MAIN_SOURCES[@]}"

log "Compiling test sources"
javac "${JAVAC_FLAGS[@]}" -cp "$MAIN_CLASSES_DIR" -d "$TEST_CLASSES_DIR" "${TEST_SOURCES[@]}"

log "Running tests"
java -cp "$MAIN_CLASSES_DIR:$TEST_CLASSES_DIR" "$TEST_CLASS"

log "Running console demo"
java -cp "$MAIN_CLASSES_DIR" "$MAIN_CLASS" "$@"

log "Completed successfully."
