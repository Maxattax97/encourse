#!/bin/bash

# Argument 1 is the directory that testall is expected to be executed from
# Argument 2 is the directory that contains all uploaded testing scripts for the assignment

DIRECTORY="$(pwd)"
mkdir -p $1
cd $1
ls $2 | while read t; do
    printf "${t}:"
    $($2/${t})
    if [ $? -eq 0 ]
    then printf "P;"
    else printf "F;"
    fi
done
cd "${DIRECTORY}"