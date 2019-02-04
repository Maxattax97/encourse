#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is name of temporary file to store in for Java

DIRECTORY="$(pwd)"
cd "$1"
git log --perl-regexp --author='^((?!CS252).*)$' --date=iso --pretty=format:"@UPDATE %H %ad" | grep "@UPDATE"
cd "${DIRECTORY}"