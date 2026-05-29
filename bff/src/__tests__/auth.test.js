const request = require('supertest')
const express = require('express')
const axios = require('axios')

jest.mock('axios')

const authRouter = require('../routes/auth')

const app = express()
app.use(express.json())
app.use('/auth', authRouter)

describe('POST /auth/login', () => {
  beforeEach(() => {
    jest.clearAllMocks()
    process.env.KEYCLOAK_URL = 'http://localhost:8080'
    process.env.KEYCLOAK_REALM = 'rednorte'
    process.env.KEYCLOAK_CLIENT_ID = 'bff-client'
    process.env.KEYCLOAK_CLIENT_SECRET = 'secret'
  })

  test('login exitoso retorna token', async () => {
    axios.post.mockResolvedValue({
      data: { access_token: 'mock-token-123' }
    })

    const res = await request(app)
      .post('/auth/login')
      .send({ username: 'admin', password: '1234' })

    expect(res.status).toBe(200)
    expect(res.body.token).toBe('mock-token-123')
  })

  test('credenciales incorrectas retorna 401', async () => {
    axios.post.mockRejectedValue({
      response: { data: { error: 'invalid_grant' } }
    })

    const res = await request(app)
      .post('/auth/login')
      .send({ username: 'admin', password: 'wrong' })

    expect(res.status).toBe(401)
    expect(res.body.error).toBeDefined()
  })
})