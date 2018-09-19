#!/bin/bash

DIRECTORY="$(pwd)"
cd "$1"
git rev-list --count master
cd "${DIRECTORY}"