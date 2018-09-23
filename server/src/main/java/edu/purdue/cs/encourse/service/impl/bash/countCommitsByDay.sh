#!/bin/bash

# Argument 1 is path to student project to count commits of, argument 2 is name of temporary file to store in for python

DIRECTORY="$(pwd)"
cd "$1"
git log --date=short --pretty=format:%ad | sort | uniq -c >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"

