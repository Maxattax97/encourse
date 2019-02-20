#!/bin/bash

# Argument 1 is the path to the student project to get testall score from
# Argument 2 is the timestamp that testall scores are taken after
# Argument 3 is the path to testall.out

DIRECTORY="$(pwd)"
cd "$1"
git log --pretty=format:"@DIFF,%H,%aI,%ce" --reverse --after="$2" -U1000 -p "$3"
cd "${DIRECTORY}"