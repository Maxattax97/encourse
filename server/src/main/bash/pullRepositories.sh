#!/bin/bash

# Argument 1 is path to course directory
# Argument 2 is path to student repository from course directory

DIRECTORY="$(pwd)"
cd "$1"
cd "$2"
git reset --hard origin/master 2> /dev/null
git pull -f origin master 2> /dev/null
cd "${DIRECTORY}"