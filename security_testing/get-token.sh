#!/bin/bash

USERNAME=${1:-admin}
PASSWORD=${2:-admin}
REALM=${3:-master}
client_id=${4:-clinicx-backend}
client_secret=${5:-SERq6fNbsj9GXaFJ2P87YthsKK64cUKp}

# Make sure jq is installed
if ! command -v jq &> /dev/null; then
  echo "❌ Error: 'jq' is not installed. Please install it first."
  exit 1
fi

# Call Keycloak token endpoint
RESPONSE=$(curl -s -X POST "http://localhost:18081/realms/$REALM/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=$client_id" \
  -d "client_secret=$client_secret" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD" \
  -d "grant_type=password")

# Check curl exit status
if [[ $? -ne 0 || -z "$RESPONSE" ]]; then
  echo "❌ Failed to get a response from Keycloak."
  exit 1
fi

# Parse access_token from response
token=$(echo "$RESPONSE" | jq -r '.access_token')

# Check if token is extracted
if [[ "$token" == "null" || -z "$token" ]]; then
  echo "❌ Failed to extract access_token."
  echo "Response: $RESPONSE"
  exit 1
fi

# Output token
echo "✅ Access token:"
echo "$token"

echo "$token" | pbcopy
