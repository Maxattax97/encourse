#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is the name of the author (usually course name)

DIRECTORY="$(pwd)"
cd "$1"
git log --author="$2" --date=iso --pretty=format:"@UPDATE %h %ad" | grep "@UPDATE"
cd "${DIRECTORY}"