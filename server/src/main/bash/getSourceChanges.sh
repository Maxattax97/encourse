#!/bin/bash

# Argument 1 is path to student project to get source changes from
# Argument 2 is the last commit hash of the previous day
# Argument 3 is the last commit hash of the chosen day
# Argument 4 is the path to file to show changes for

DIRECTORY="$(pwd)"
cd "$1"
#if [[ $# -eq 2 ]]
    git diff -U10000 "$2~" "$2"
#fi
#if [[ $# -eq 3 ]]
#    then git diff -U10000 "$2" "$3"
#    else git diff -U10000 "$2" "$3" -p "$4"
#fi
cd "${DIRECTORY}"