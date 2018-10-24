#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python
# Argument 3 is the username for the student that git is logging

DIRECTORY="$(pwd)"
cd "$1"
printf "$3 " >> "${DIRECTORY}/$2"
git log --perl-regexp --author='^((?!CS252).*)$' --pretty=format:%ad | wc -l >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"
