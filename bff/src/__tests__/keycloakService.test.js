const axios = require('axios')

jest.mock('axios')

// Env vars deben estar antes de require para que los consts del módulo los capturen
process.env.KEYCLOAK_URL = 'http://keycloak:8080'
process.env.KEYCLOAK_REALM = 'rednorte'
process.env.KEYCLOAK_CLIENT_ID = 'rednorte-api'
process.env.KEYCLOAK_CLIENT_SECRET = 'rednorte-secret'

const { crearUsuario, asignarRol, limpiarAccionesRequeridas } = require('../services/keycloakService')

beforeEach(() => jest.clearAllMocks())

// ─── crearUsuario ─────────────────────────────────────────────────────────────

describe('keycloakService.crearUsuario', () => {
  test('obtiene token de admin y crea el usuario en Keycloak', async () => {
    axios.post
      .mockResolvedValueOnce({ data: { access_token: 'admin-token' } }) // adminToken
      .mockResolvedValueOnce({ headers: { location: 'http://keycloak/users/abc-123' } }) // crearUsuario

    const usuario = { username: '12345678-9', enabled: true }
    const res = await crearUsuario(usuario)

    // Primer POST: obtener token de admin (grant_type client_credentials)
    expect(axios.post).toHaveBeenNthCalledWith(
      1,
      expect.stringContaining('/protocol/openid-connect/token'),
      expect.stringContaining('client_credentials'),
      expect.objectContaining({ headers: { 'Content-Type': 'application/x-www-form-urlencoded' } })
    )
    // Segundo POST: crear usuario
    expect(axios.post).toHaveBeenNthCalledWith(
      2,
      expect.stringContaining('/admin/realms/rednorte/users'),
      usuario,
      expect.objectContaining({ headers: expect.objectContaining({ Authorization: 'Bearer admin-token' }) })
    )
    expect(res.headers.location).toContain('abc-123')
  })

  test('propaga el error si la creación del usuario falla', async () => {
    axios.post
      .mockResolvedValueOnce({ data: { access_token: 'admin-token' } })
      .mockRejectedValueOnce(new Error('User already exists'))

    await expect(crearUsuario({ username: '12345678-9' })).rejects.toThrow('User already exists')
  })

  test('propaga el error si falla la obtención del token de admin', async () => {
    axios.post.mockRejectedValueOnce(new Error('Keycloak unreachable'))

    await expect(crearUsuario({})).rejects.toThrow('Keycloak unreachable')
  })
})

// ─── asignarRol ───────────────────────────────────────────────────────────────

describe('keycloakService.asignarRol', () => {
  test('obtiene token, busca el rol y lo asigna al usuario', async () => {
    axios.post
      .mockResolvedValueOnce({ data: { access_token: 'admin-token' } }) // adminToken
      .mockResolvedValueOnce({}) // POST role-mappings
    axios.get.mockResolvedValueOnce({ data: { id: 'role-id', name: 'PACIENTE' } }) // GET rol

    await asignarRol('user-abc', 'PACIENTE')

    expect(axios.get).toHaveBeenCalledWith(
      expect.stringContaining('/roles/PACIENTE'),
      expect.objectContaining({ headers: { Authorization: 'Bearer admin-token' } })
    )
    expect(axios.post).toHaveBeenNthCalledWith(
      2,
      expect.stringContaining('/users/user-abc/role-mappings/realm'),
      [{ id: 'role-id', name: 'PACIENTE' }],
      expect.any(Object)
    )
  })

  test('propaga el error si la búsqueda del rol falla', async () => {
    axios.post.mockResolvedValueOnce({ data: { access_token: 'admin-token' } })
    axios.get.mockRejectedValueOnce(new Error('Role not found'))

    await expect(asignarRol('user-abc', 'INEXISTENTE')).rejects.toThrow('Role not found')
  })

  test('propaga el error si la asignación del rol falla', async () => {
    axios.post
      .mockResolvedValueOnce({ data: { access_token: 'admin-token' } })
      .mockRejectedValueOnce(new Error('Assignment failed'))
    axios.get.mockResolvedValueOnce({ data: { id: 'role-id', name: 'PACIENTE' } })

    await expect(asignarRol('user-abc', 'PACIENTE')).rejects.toThrow('Assignment failed')
  })
})

// ─── limpiarAccionesRequeridas ────────────────────────────────────────────────

describe('keycloakService.limpiarAccionesRequeridas', () => {
  test('obtiene token y actualiza el usuario limpiando requiredActions', async () => {
    axios.post.mockResolvedValueOnce({ data: { access_token: 'admin-token' } })
    axios.put.mockResolvedValueOnce({})

    await limpiarAccionesRequeridas('user-abc')

    expect(axios.put).toHaveBeenCalledWith(
      expect.stringContaining('/admin/realms/rednorte/users/user-abc'),
      { requiredActions: [], emailVerified: true },
      expect.objectContaining({ headers: expect.objectContaining({ Authorization: 'Bearer admin-token' }) })
    )
  })

  test('propaga el error si el PUT falla', async () => {
    axios.post.mockResolvedValueOnce({ data: { access_token: 'admin-token' } })
    axios.put.mockRejectedValueOnce(new Error('Update failed'))

    await expect(limpiarAccionesRequeridas('user-abc')).rejects.toThrow('Update failed')
  })
})

// ─── adminToken — guard de configuración ─────────────────────────────────────

describe('adminToken — config incompleta', () => {
  test('lanza error si KEYCLOAK_URL no está definida', async () => {
    let serviceNoConfig
    const url = process.env.KEYCLOAK_URL
    delete process.env.KEYCLOAK_URL

    jest.isolateModules(() => {
      serviceNoConfig = require('../services/keycloakService')
    })

    process.env.KEYCLOAK_URL = url

    await expect(serviceNoConfig.crearUsuario({})).rejects.toThrow('Keycloak admin configuration incompleta')
  })
})
