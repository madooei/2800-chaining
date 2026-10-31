#!/usr/bin/env bash
# Compile all source into out/ and run a demo.
# With no argument, run the HashMap demo (map.Main). With one argument, run
# that class instead, e.g.:
#   scripts/run.sh adaptiveheap.Main
set -e
cd "$(dirname "$0")/.."
javac -d out $(find src/main -name "*.java")
if [ $# -eq 0 ]; then
  java -cp out map.Main
else
  java -cp out "$1"
fi
