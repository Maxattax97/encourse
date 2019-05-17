#!/bin/bash

# Argument 1 is path to student directory, argument 2 is path to the .git file to clone

DIRECTORY="$(pwd)"
cd "$1"
git clone "ssh://cs252@data.cs.purdue.edu$2"
cd "${DIRECTORY}"