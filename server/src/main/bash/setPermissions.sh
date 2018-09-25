#!/bin/bash

# Argument 1 is the courseID to modify the permissions for

DIRECTORY="$(pwd)"
cd "/sourcecontrol"
chmod -R 770 "$1"
chgrp -R "$1" "$1"
cd "${DIRECTORY}"