#!/bin/bash -e

echo "starting boot2docker"
sudo boot2docker start

echo "Read docker env"
eval "$(sudo boot2docker shellinit)"

echo "Run ldap server"
docker run -d -p 389:389 osixia/openldap

echo "port forward ldap"
sudo boot2docker ssh -vnNTL 389:localhost:389