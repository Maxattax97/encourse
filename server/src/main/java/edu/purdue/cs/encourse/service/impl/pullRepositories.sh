#!/bin/bash

DIRECTORY="$(pwd)"
cd "$1"
git reset --hard
git pull
cd "${DIRECTORY}"