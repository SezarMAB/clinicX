#!/bin/bash
  USERNAME=${1:-admin}
  PASSWORD=${2:-admin}
  REALM=${3:-master}
  client_secret=${4:-SERq6fNbsj9GXaFJ2P87YthsKK64cUKp}

#TODO how to fetch the client secret dynamically?
  RESPONSE=$(curl -s -X POST http://localhost:18081/realms/$REALM/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=clinicx-backend" \
    -d "client_secret=$client_secret" \
    -d "username=$USERNAME" \
    -d "password=$PASSWORD" \
    -d "grant_type=password")

  # Extract just the access token
#  echo $RESPONSE
#  echo "--------------------"
  echo $RESPONSE | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
