#!/bin/bash

# Argument 1 is path to first student in diff
# Argument 2 is path to second student in diff
# Argument 3 is the hash diffs are based on

# DIRECTORY="$(pwd)"
# cs252 last commit hash 6f284e205ac396c895ca42ca200105fb86822b7b

# only works for cs252 shell project

declare -a arr=("shell.y" "shell.l" "shell.cc")
out=0

# loop through array
for i in "${arr[@]}"
do
    # specific file diff
    user1=$(git --git-dir="$1/.git" diff --ignore-all-space "$3:$i" "HEAD:$i" | grep -e '^[+]' | grep -ve '^[+]\{3\}')
    user2=$(git --git-dir="$2/.git" diff --ignore-all-space "$3:$i" "HEAD:$i" | grep -e '^[+]' | grep -ve '^[+]\{3\}')
    temp=$(grep -xFf <(echo "$user1") <(echo "$user2") | grep -vE '^\+\s*[{}]?$' | grep -vE '^\+\s*(return|#include|else).*$' | grep -vE '^\+\s*(/\*|\*/|;)$' | wc -l)
    out=$((out + temp))
done

echo "$out"
