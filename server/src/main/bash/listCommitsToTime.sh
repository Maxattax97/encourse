#!/bin/bash

# Argument 1 is path to student project to count commits of
# Argument 2 is the timestamp of the first commit to retrieve

# NOTE: Format of output from this is:
#
# @UPDATE      Commit Hash       Timestamp
# etc...
#

DIRECTORY="$(pwd)"
cd "$1"
git log --perl-regexp --author='^((?!CS252).*)$' --date=short --pretty=format:"@UPDATE %H %ad" --reverse --after="$2"
cd "${DIRECTORY}"
