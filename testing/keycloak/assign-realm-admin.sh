#!/bin/sh

KEYCLOAK_URL="http://keycloak:8080"
REALM="rednorte"
ADMIN_USER="admin"
ADMIN_PASS="admin123"
SA_CLIENT_ID="rednorte-api"

# 1. Wait for Keycloak to be ready
echo "Waiting for Keycloak..."
MAX_ATTEMPTS=60
ATTEMPT=0
until curl -sf "${KEYCLOAK_URL}/realms/master" > /dev/null 2>&1; do
  ATTEMPT=$((ATTEMPT + 1))
  if [ "$ATTEMPT" -ge "$MAX_ATTEMPTS" ]; then
    echo "Keycloak did not become ready after ${MAX_ATTEMPTS} attempts. Exiting."
    exit 1
  fi
  echo "  Not ready yet (attempt ${ATTEMPT}/${MAX_ATTEMPTS}), retrying in 10s..."
  sleep 10
done
echo "Keycloak is ready."
echo "Waiting 30s for realm import to complete..."
sleep 30

# 2. Obtain admin token from master realm using admin-cli (password grant)
TOKEN=$(curl -sf \
  -X POST "${KEYCLOAK_URL}/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password&client_id=admin-cli&username=${ADMIN_USER}&password=${ADMIN_PASS}" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)
echo "Admin token obtained."

# 3. Resolve internal UUID of rednorte-api client
CLIENT_UUID=$(curl -sf \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=${SA_CLIENT_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "rednorte-api UUID: ${CLIENT_UUID}"

# 4. Get the service account user ID for rednorte-api
SA_USER_ID=$(curl -sf \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${CLIENT_UUID}/service-account-user" \
  -H "Authorization: Bearer ${TOKEN}" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "Service account user ID: ${SA_USER_ID}"

# 5. Resolve internal UUID of realm-management client
RM_UUID=$(curl -sf \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/clients?clientId=realm-management" \
  -H "Authorization: Bearer ${TOKEN}" \
  | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "realm-management UUID: ${RM_UUID}"

# 6. Get the realm-admin role representation from realm-management
ROLE_JSON=$(curl -sf \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/clients/${RM_UUID}/roles/realm-admin" \
  -H "Authorization: Bearer ${TOKEN}")
ROLE_ID=$(echo "${ROLE_JSON}" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
ROLE_NAME=$(echo "${ROLE_JSON}" | grep -o '"name":"[^"]*"' | head -1 | cut -d'"' -f4)
echo "realm-admin role: id=${ROLE_ID}, name=${ROLE_NAME}"

# 7. Assign realm-admin client role to the service account
curl -sf \
  -X POST \
  "${KEYCLOAK_URL}/admin/realms/${REALM}/users/${SA_USER_ID}/role-mappings/clients/${RM_UUID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "[{\"id\":\"${ROLE_ID}\",\"name\":\"${ROLE_NAME}\"}]"

echo "Done: realm-admin assigned to service account of ${SA_CLIENT_ID}."
