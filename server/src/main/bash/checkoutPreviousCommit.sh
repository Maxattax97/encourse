#!/bin/bash

# Argument 1 is path to student project to pull
# Argument 2 is the commit hash to checkout

DIRECTORY="$(pwd)"
cd "$1"
git checkout -f "$2"
rm Makefile
cd "${DIRECTORY}"