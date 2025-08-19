#!/usr/bin/env bash

# Interactive Keycloak token fetcher with realm/user presets.
# To add more realms or users, edit the CONFIG JSON below.

# -------------------------
# Editable configuration
# -------------------------
CONFIG='
{
  "kc_base_url": "http://localhost:18081",
  "realms": {
    "dental-realm": {
      "client_id": "clinicx-backend",
      "client_secret": "6a6d20c7-439f-41a4-9013-2070458548c3",
      "users": {
        "saleh": "admin123",
        "ali":   "Admin123",
        "ibro":  "Admin123"
      }
    },
    "appointments-realm": {
      "client_id": "clinicx-backend",
      "client_secret": "6a6d20c7-439f-41a4-9013-20704585482x",
      "users": {
        "anas":  "admin123",
        "ahmad": "Admin123",
        "maria": "Admin123"
      }
    }
  }
}
'

# -------------------------
# Pre-flight checks
# -------------------------
if ! command -v jq &> /dev/null; then
  echo "❌ Error: 'jq' is not installed. Please install it first."
  exit 1
fi

if ! command -v curl &> /dev/null; then
  echo "❌ Error: 'curl' is not installed. Please install it first."
  exit 1
fi

KC_BASE_URL="$(jq -r '.kc_base_url' <<< "$CONFIG")"
if [[ -z "$KC_BASE_URL" || "$KC_BASE_URL" == "null" ]]; then
  echo "❌ Error: kc_base_url is not set in CONFIG."
  exit 1
fi

# -------------------------
# Select realm (no mapfile)
# -------------------------
REALM_OPTIONS="$(jq -r '.realms | keys[]' <<< "$CONFIG")"
if [[ -z "$REALM_OPTIONS" ]]; then
  echo "❌ Error: No realms defined in CONFIG."
  exit 1
fi

echo "Select a realm:"
PS3="Enter choice: "
select REALM in $REALM_OPTIONS; do
  if [[ -n "$REALM" ]]; then
    break
  else
    echo "Invalid selection. Try again."
  fi
done

# Validate realm config
CLIENT_ID="$(jq -r ".realms[\"$REALM\"].client_id" <<< "$CONFIG")"
CLIENT_SECRET="$(jq -r ".realms[\"$REALM\"].client_secret" <<< "$CONFIG")"
if [[ -z "$CLIENT_ID" || "$CLIENT_ID" == "null" || -z "$CLIENT_SECRET" || "$CLIENT_SECRET" == "null" ]]; then
  echo "❌ Error: client_id/client_secret not configured for realm '$REALM'."
  exit 1
fi

# -------------------------
# Select user (no mapfile)
# -------------------------
USER_OPTIONS="$(jq -r ".realms[\"$REALM\"].users | keys[]" <<< "$CONFIG")"
if [[ -z "$USER_OPTIONS" ]]; then
  echo "❌ Error: No users configured for realm '$REALM'."
  exit 1
fi

echo "Select a user in realm '$REALM':"
select USERNAME in $USER_OPTIONS; do
  if [[ -n "$USERNAME" ]]; then
    break
  else
    echo "Invalid selection. Try again."
  fi
done

PASSWORD="$(jq -r ".realms[\"$REALM\"].users[\"$USERNAME\"]" <<< "$CONFIG")"
if [[ -z "$PASSWORD" || "$PASSWORD" == "null" ]]; then
  echo "❌ Error: Password not configured for user '$USERNAME' in realm '$REALM'."
  exit 1
fi

echo "➡️  Getting token for:"
echo "   Realm     : $REALM"
echo "   User      : $USERNAME"
echo "   Client ID : $CLIENT_ID"
# Do not echo secrets/passwords

# -------------------------
# Request token
# -------------------------
TOKEN_ENDPOINT="$KC_BASE_URL/realms/$REALM/protocol/openid-connect/token"

RESPONSE=$(curl -sS -X POST "$TOKEN_ENDPOINT" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data-urlencode "client_id=$CLIENT_ID" \
  --data-urlencode "client_secret=$CLIENT_SECRET" \
  --data-urlencode "username=$USERNAME" \
  --data-urlencode "password=$PASSWORD" \
  -d "grant_type=password")

if [[ $? -ne 0 || -z "$RESPONSE" ]]; then
  echo "❌ Failed to get a response from Keycloak."
  exit 1
fi

TOKEN=$(echo "$RESPONSE" | jq -r '.access_token // empty')
if [[ -z "$TOKEN" ]]; then
  ERROR_MSG=$(echo "$RESPONSE" | jq -r '.error_description // .error // "Unknown error"')
  echo "❌ Failed to extract access_token. Server said: $ERROR_MSG"
  echo "Raw response:"
  echo "$RESPONSE"
  exit 1
fi

echo "✅ Access token:"
echo "$TOKEN"

# -------------------------
# Copy to clipboard if possible
# -------------------------
if command -v pbcopy &> /dev/null; then
  printf "%s" "$TOKEN" | pbcopy
  echo "📋 Token copied to clipboard (pbcopy)."
elif command -v xclip &> /dev/null; then
  printf "%s" "$TOKEN" | xclip -selection clipboard
  echo "📋 Token copied to clipboard (xclip)."
elif command -v wl-copy &> /dev/null; then
  printf "%s" "$TOKEN" | wl-copy
  echo "📋 Token copied to clipboard (wl-copy)."
else
  echo "ℹ️ Clipboard tool not found. Install pbcopy (macOS), xclip (X11), or wl-copy (Wayland) if you want auto-copy."
fi

# Example API call (uncomment and adjust as needed)
# curl -X GET "http://localhost:8080/api/patients" \
#   -H "API-Version: 1" \
#   -H "Authorization: Bearer $TOKEN"
