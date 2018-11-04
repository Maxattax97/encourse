#!/bin/bash

# Argument 1 is path to student project to pull

DIRECTORY="$(pwd)"
cd "$1"
git reset --hard origin/master
git pull origin master
cd "${DIRECTORY}"