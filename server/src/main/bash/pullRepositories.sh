#!/bin/bash

# Argument 1 is path to student project to pull

DIRECTORY="$(pwd)"
cd "$1"
git reset --hard origin/master 2> /dev/null
git pull -f origin master 2> /dev/null
cd "${DIRECTORY}"