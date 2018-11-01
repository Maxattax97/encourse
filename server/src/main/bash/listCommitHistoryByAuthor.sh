#!/usr/bin/env bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python
# Argument 3 is the name of the author (usually course name)

DIRECTORY="$(pwd)"
cd "$1"
git log --author="$3" --date=iso --pretty=format:"@UPDATE %h %ad" | grep "@UPDATE" > "${DIRECTORY}/$2"
cd "${DIRECTORY}"