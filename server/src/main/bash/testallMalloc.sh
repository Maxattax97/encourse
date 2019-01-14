#!/bin/bash

# Argument 1 is the directory that testall is expected to be executed from
# Argument 2 is the directory that contains all uploaded testing scripts for the assignment

DIRECTORY="$(pwd)"
cd $1
rm -rf tests
rm runtest.py
rm testing.h
rm testing.c
cp -R "$2/tests" .
cp "$2/runtest.py" .
cp "$2/testing.h" .
cp "$2/testing.c" .
runtest.py | grep "TEST:" | awk '{printf $2 ":" substr($3,1,1) ";"}' 2> /dev/null
cd "${DIRECTORY}"
