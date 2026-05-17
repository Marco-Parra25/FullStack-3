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

const asignarRol = async (userId, roleName) => {
  const token = await adminToken()
  const adminUrl = `${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}`

  const rolRes = await axios.get(`${adminUrl}/roles/${roleName}`, {
    headers: { Authorization: `Bearer ${token}` }
  })

  await axios.post(
    `${adminUrl}/users/${userId}/role-mappings/realm`,
    [rolRes.data],
    {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  )
}

const limpiarAccionesRequeridas = async (userId) => {
  const token = await adminToken()
  const adminUrl = `${KEYCLOAK_URL}/admin/realms/${KEYCLOAK_REALM}`
  await axios.put(
    `${adminUrl}/users/${userId}`,
    { requiredActions: [], emailVerified: true },
    { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
  )
}

module.exports = { crearUsuario, asignarRol, limpiarAccionesRequeridas }
