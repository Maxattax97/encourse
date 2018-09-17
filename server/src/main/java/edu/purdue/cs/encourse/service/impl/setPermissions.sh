#!/bin/bash

DIRECTORY="$(pwd)"
cd "/sourcecontrol"
chmod -R 770 "$1"
chgrp -R "$1" "$1"
cd "${DIRECTORY}"