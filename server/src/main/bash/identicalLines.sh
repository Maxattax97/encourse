#!/bin/bash

# WORK IN PROGRESS
# Argument 1 is path to student project 1
# Argument 2 is path to student project 2
# Argument 3 is name of temporary file to store in for python
# Argument 4 is the username for the student 1
# Argument 5 is the username for the student 2
# Argument 6 is the commit hash for the file

# TODO clean input arguments

# DIRECTORY="$(pwd)"
user1=$(git --git-dir=$1/.git diff --ignore-all-space 6f284e205ac396c895ca42ca200105fb86822b7b HEAD | grep -e '^[+]' | grep -ve '^[+]\{3\}')
user2=$(git --git-dir=$2/.git diff --ignore-all-space 6f284e205ac396c895ca42ca200105fb86822b7b HEAD | grep -e '^[+]' | grep -ve '^[+]\{3\}')

out=$(grep -xFf <("$user1") <("$user2") | grep -vE '^\+\s*[{}]?$' | grep -vE '^\+\s*(return|#include).+$' | wc -l)

echo "$user1;$user2;$out" >> "$3"
