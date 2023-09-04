#!/bin/bash

./gradlew clean build
rm -rf release
mkdir release
cp build/libs/quranic-corpus-api-1.0.0-all.jar release/quranic-corpus-api.jar
cp prod/* release
scp -i ../../dev/keys/fasthosts -r release/* admin-user@hunna.app:/var/www/qurancorpus.app/services
