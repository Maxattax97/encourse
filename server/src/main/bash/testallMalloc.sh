#!/bin/bash

# Argument 1 is the directory that testall is expected to be executed from
# Argument 2 is the directory that contains all uploaded testing scripts for the assignment

DIRECTORY="$(pwd)"
cd $1
rm -rf tests/expected
rm -rf tests/testsrc
rm -rf utils
rm tests/Makefile
rm runtest.py
rm testing.h
rm testing.c
rm printing.h
rm printing.c
cp -R "$2/tests/expected" .
cp -R "$2/tests/testsrc" .
cp -R "$2/utils" .
cp "$2/tests/Makefile" .
cp "$2/runtest.py" .
cp "$2/testing.h" .
cp "$2/testing.c" .
cp "$2/printing.h" .
cp "$2/printing.c" .
runtest.py | grep "TEST:" | awk '{printf $2 ":" substr($3,1,1) ";"}' 2> /dev/null
cd "${DIRECTORY}"
