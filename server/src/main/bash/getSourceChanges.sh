#!/bin/bash

# Argument 1 is path to student project to get source changes from
# Argument 2 is name of temporary file to store in for serving
# Argument 3 is the last commit hash of the previous day
# Argument 4 is the last commit hash of the chosen day
# Argument 5 is the path to file to show changes for

DIRECTORY="$(pwd)"
cd "$1"
#git show -U10000 "$3" -- "$4" >> "${DIRECTORY}/$2"
git diff -U10000 "$3" "$4" -p "$5" >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"