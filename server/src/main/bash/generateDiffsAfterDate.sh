#!/bin/bash

# Argument 1 is the path to the student project to get diffs from
# Argument 2 is the timestamp that diffs are generated after

DIRECTORY="$(pwd)"
cd "$1"
git log --perl-regexp --author='^((?!CS252).*)$' --pretty=format:"%H" --reverse --after="$2" | awk '{print $0}' | while read p; do
    git show -U0 "$p" --date=short --pretty=format:"@DIFF,%H,%aI" --ignore-all-space | awk '{gsub(" ", "", $0);gsub("\t", "", $0); print}'
done
cd "${DIRECTORY}"