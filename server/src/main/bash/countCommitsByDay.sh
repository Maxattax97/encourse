#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python
# Argument 3 is the username for the student that git is logging

DIRECTORY="$(pwd)"
cd "$1"
echo "Start $3" >> "${DIRECTORY}/$2"
git log --perl-regexp --author='^((?!CS252).*)$' --date=short --pretty=format:%ad | sort | uniq -c >> "${DIRECTORY}/$2"
echo "End $3" >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"

