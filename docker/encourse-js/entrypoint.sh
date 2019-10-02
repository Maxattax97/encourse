#!/usr/bin/env sh

cd /encourse/client/ \
    && npm install \
    && npm run build \
    && serve -s build
