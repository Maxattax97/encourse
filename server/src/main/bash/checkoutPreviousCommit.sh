#!/bin/bash

# Argument 1 is path to student project to pull
# Argument 2 is the commit hash to checkout

DIRECTORY="$(pwd)"
cd "$1"
git checkout "$2"
cd "${DIRECTORY}"