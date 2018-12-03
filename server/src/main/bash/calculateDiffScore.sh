#!/bin/bash

# Argument 1 is path to first student in diff
# Argument 2 is path to second student in diff
# Argument 3 is the hash diffs are based on

# DIRECTORY="$(pwd)"
# cs252 last commit hash 6f284e205ac396c895ca42ca200105fb86822b7b
user1=$(git --git-dir="$1/.git" diff --ignore-all-space "$3" HEAD | grep -e '^[+]' | grep -ve '^[+]\{3\}')
user2=$(git --git-dir="$2/.git" diff --ignore-all-space "$3" HEAD | grep -e '^[+]' | grep -ve '^[+]\{3\}')
out=$(grep -xFf <(echo "$user1") <(echo "$user2") | grep -vE '^\+\s*[{}]?$' | grep -vE '^\+\s*(return|#include).+$' | wc -l)

echo "$out"
