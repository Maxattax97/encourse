#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python
# Argument 3 is the username for the student that git is logging

# NOTE: Format of output from this is:
#
# - - - - - Date and Time - - - - -
# Insertions   Deletions   Filename1
# Insertions   Deletions   Filename2
# Insertions   Deletions   Filename3
# etc...
#

DIRECTORY="$(pwd)"
cd "$1"
echo "Start $3" >> "${DIRECTORY}/$2"
git log --perl-regexp --author='^((?!CS252).*)$' --reverse --numstat --date=iso --pretty=format:"%aI %h" >> "${DIRECTORY}/$2"
echo "End $3" >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"
