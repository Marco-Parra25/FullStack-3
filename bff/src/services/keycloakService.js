const axios = require('axios')
const qs = require('querystring')

const KEYCLOAK_URL = process.env.KEYCLOAK_URL
const KEYCLOAK_REALM = process.env.KEYCLOAK_REALM
const KEYCLOAK_CLIENT_ID = process.env.KEYCLOAK_CLIENT_ID
const KEYCLOAK_CLIENT_SECRET = process.env.KEYCLOAK_CLIENT_SECRET

const adminToken = async () => {
  if (!KEYCLOAK_URL || !KEYCLOAK_REALM || !KEYCLOAK_CLIENT_ID || !KEYCLOAK_CLIENT_SECRET) {
    throw new Error('Keycloak admin configuration incompleta')
  }

  const response = await axios.post(
    `${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}/protocol/openid-connect/token`,
    qs.stringify({
      grant_type: 'client_credentials',
      client_id: KEYCLOAK_CLIENT_ID,
      client_secret: KEYCLOAK_CLIENT_SECRET
    }),
    { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
  )

  return response.data.access_token
}

const crearUsuario = async (usuario) => {
  const token = await adminToken()
  const adminUrl = `${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}`

  return axios.post(`${adminUrl}/users`, usuario, {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  })
}

module.exports = {
  crearUsuario
}
