#!/bin/bash

# Argument 1 is path to directory the shall be cleaned

DIRECTORY="$(pwd)"
cd "$1"
rm *.txt
cd "${DIRECTORY}"