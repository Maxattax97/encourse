#!/bin/bash

# Argument 1 is the course account to run command under
# Argument 2 is the testall script to run
# Argument 3 is the directory that testall is expected to be executed from
# Argument 4 is the directory that contains all uploaded testing scripts for the assignment

sudo -u $1 $2 $3 $4 2> /dev/null