#!/bin/bash

# Argument 1 is the directory that make is expected to be executed from
# Argument 2 is the filepath for the Makefile

DIRECTORY="$(pwd)"
mkdir -p $1
cd $1
cp $2 $1
make
cd "${DIRECTORY}"