#!/bin/bash

# Argument 1 is the course account to run command under
# Argument 2 is the command (including arguments) to execute as a course account

sudo -u $1 $2