#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for python

DIRECTORY="$(pwd)"
cd "$1"
git shortlog --perl-regexp --author='^((?!CS252).*)$' -p --format="%h %ai" test-shell/testall.out >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"