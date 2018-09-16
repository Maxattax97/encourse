#!/bin/bash

DIRECTORY="$(pwd)"
cd "$1"
git clone "ssh://reed226@data.cs.purdue.edu$2"
cd "${DIRECTORY}"