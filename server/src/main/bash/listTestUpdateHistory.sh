#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python

DIRECTORY="$(pwd)"
cd "$1"
rm "${DIRECTORY}/$2"
git log --perl-regexp --author='^((?!CS252).*)$' --date=iso --pretty=format:"@UPDATE %h %ad" -p test-shell/testall.out | grep "@UPDATE" > "${DIRECTORY}/$2"
cd "${DIRECTORY}"