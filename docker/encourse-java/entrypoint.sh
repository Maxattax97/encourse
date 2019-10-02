#!/usr/bin/env sh

cd /encourse/server/ \
    && mvn clean package install \
    && mvn spring-boot:run
