#!/bin/bash
  USERNAME=${1:-admin}
  PASSWORD=${2:-admin}

  RESPONSE=$(curl -s -X POST http://localhost:18081/realms/clinicx-dev/protocol/openid-connect/token \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "client_id=clinicx-backend" \
    -d "client_secret=tHFYKZ1bgyLz6dOWHsAa8ir3yXdzbcjk" \
    -d "username=$USERNAME" \
    -d "password=$PASSWORD" \
    -d "grant_type=password")

  # Extract just the access token
  echo $RESPONSE | grep -o '"access_token":"[^"]*' | cut -d'"' -f4
