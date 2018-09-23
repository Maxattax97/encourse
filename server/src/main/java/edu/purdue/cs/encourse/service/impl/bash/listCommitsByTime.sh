#!/bin/bash

# Argument 1 is path to student project to count commits of, argument 2 is name of temporary file to store in for python

DIRECTORY="$(pwd)"
cd "$1"
git log --date=iso --pretty=format:%ad >> "${DIRECTORY}/$2"
cd "${DIRECTORY}"
