#!/bin/bash

# Argument 1 is the course account to kill proccesses for a course account

sudo pkill -u "$1-account"