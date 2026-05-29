const request = require('supertest')
const express = require('express')

jest.mock('../services/listaEsperaService')
jest.mock('../services/keycloakService')

const service = require('../services/listaEsperaService')
const keycloakService = require('../services/keycloakService')
const adminRouter = require('../routes/admin')

const app = express()
app.use(express.json())
app.use('/admin', adminRouter)

beforeEach(() => jest.clearAllMocks())

// ─── GET /admin/lista ─────────────────────────────────────────────────────────

describe('GET /admin/lista', () => {
  test('retorna pacientes y total en espera', async () => {
    service.listarPorPrioridad.mockResolvedValue({ data: [{ id: 1, pacienteNombre: 'Juan' }] })
    service.contarEnEspera.mockResolvedValue({ data: { enEspera: 5 } })

    const res = await request(app).get('/admin/lista').set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body.pacientes).toHaveLength(1)
    expect(res.body.totalEnEspera).toBe(5)
  })

  test('retorna 503 si el servicio no está disponible', async () => {
    service.listarPorPrioridad.mockRejectedValue({ response: { status: 503 } })

    const res = await request(app).get('/admin/lista')

    expect(res.status).toBe(503)
  })

  test('retorna 401 si el token es inválido', async () => {
    service.listarPorPrioridad.mockRejectedValue({ response: { status: 401 } })

    const res = await request(app).get('/admin/lista')

    expect(res.status).toBe(401)
  })
})

// ─── POST /admin/registrar ────────────────────────────────────────────────────

describe('POST /admin/registrar', () => {
  test('registra exitosamente y retorna 201', async () => {
    service.registrar.mockResolvedValue({ data: { id: 1, pacienteRut: '12345678-9' } })

    const res = await request(app)
      .post('/admin/registrar')
      .send({ pacienteRut: '12345678-9', especialidad: 'Cardiología' })

    expect(res.status).toBe(201)
    expect(res.body.id).toBe(1)
  })

  test('retorna 404 si el paciente no existe en el sistema', async () => {
    service.registrar.mockRejectedValue({ response: { status: 404 } })

    const res = await request(app).post('/admin/registrar').send({})

    expect(res.status).toBe(404)
    expect(res.body.error).toBe('Paciente no encontrado')
  })

  test('retorna 500 si el servicio falla', async () => {
    service.registrar.mockRejectedValue({})

    const res = await request(app).post('/admin/registrar').send({})

    expect(res.status).toBe(500)
  })
})

// ─── PATCH /admin/cancelar/:id ────────────────────────────────────────────────

describe('PATCH /admin/cancelar/:id', () => {
  test('cancela exitosamente y devuelve mensaje de confirmación', async () => {
    service.cancelar.mockResolvedValue({})

    const res = await request(app)
      .patch('/admin/cancelar/1')
      .set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body.mensaje).toMatch(/cancelada/i)
  })

  test('retorna 403 si no tiene permisos', async () => {
    service.cancelar.mockRejectedValue({ response: { status: 403 } })

    const res = await request(app).patch('/admin/cancelar/1')

    expect(res.status).toBe(403)
  })

  test('retorna 500 si el servicio falla', async () => {
    service.cancelar.mockRejectedValue({})

    const res = await request(app).patch('/admin/cancelar/1')

    expect(res.status).toBe(500)
  })
})

// ─── PATCH /admin/estado/:id ─────────────────────────────────────────────────

describe('PATCH /admin/estado/:id', () => {
  test('actualiza estado y retorna la ficha actualizada', async () => {
    service.actualizarEstado.mockResolvedValue({ data: { id: 1, estado: 'ATENDIDO' } })

    const res = await request(app)
      .patch('/admin/estado/1')
      .send({ estado: 'ATENDIDO' })

    expect(res.status).toBe(200)
    expect(res.body.estado).toBe('ATENDIDO')
  })

  test('retorna 401 si el token es inválido', async () => {
    service.actualizarEstado.mockRejectedValue({ response: { status: 401 } })

    const res = await request(app).patch('/admin/estado/1').send({ estado: 'ATENDIDO' })

    expect(res.status).toBe(401)
  })

  test('retorna 500 si el servicio falla', async () => {
    service.actualizarEstado.mockRejectedValue({})

    const res = await request(app).patch('/admin/estado/1').send({})

    expect(res.status).toBe(500)
  })
})

// ─── GET /admin/pacientes ─────────────────────────────────────────────────────

describe('GET /admin/pacientes', () => {
  test('retorna la lista de pacientes', async () => {
    service.listarPacientes.mockResolvedValue({ data: [{ id: 1, nombre: 'Ana' }, { id: 2, nombre: 'Luis' }] })

    const res = await request(app).get('/admin/pacientes').set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body).toHaveLength(2)
  })

  test('retorna 503 si el servicio no está disponible', async () => {
    service.listarPacientes.mockRejectedValue({ response: { status: 503 } })

    const res = await request(app).get('/admin/pacientes')

    expect(res.status).toBe(503)
  })

  test('retorna 500 si el servicio falla sin código conocido', async () => {
    service.listarPacientes.mockRejectedValue({})

    const res = await request(app).get('/admin/pacientes')

    expect(res.status).toBe(500)
  })
})

// ─── POST /admin/pacientes ────────────────────────────────────────────────────

describe('POST /admin/pacientes', () => {
  test('crea paciente y usuario Keycloak con roles asignados', async () => {
    service.crearPaciente.mockResolvedValue({ data: { id: 1, nombre: 'Pedro' } })
    keycloakService.crearUsuario.mockResolvedValue({
      headers: { location: 'http://keycloak/admin/realms/rednorte/users/abc-123' }
    })
    keycloakService.asignarRol.mockResolvedValue({})
    keycloakService.limpiarAccionesRequeridas.mockResolvedValue({})

    const res = await request(app)
      .post('/admin/pacientes')
      .send({ rut: '12345678-9', nombre: 'Pedro', email: 'pedro@test.com' })

    expect(res.status).toBe(201)
    expect(res.body.id).toBe(1)
    expect(res.body.aviso).toBeUndefined()
    expect(keycloakService.asignarRol).toHaveBeenCalledWith('abc-123', 'PACIENTE')
    expect(keycloakService.asignarRol).toHaveBeenCalledWith('abc-123', 'USER')
    expect(keycloakService.limpiarAccionesRequeridas).toHaveBeenCalledWith('abc-123')
  })

  test('crea paciente sin RUT retorna 201 con aviso', async () => {
    service.crearPaciente.mockResolvedValue({ data: { id: 2, nombre: 'Maria' } })

    const res = await request(app)
      .post('/admin/pacientes')
      .send({ nombre: 'Maria', email: 'maria@test.com' })

    expect(res.status).toBe(201)
    expect(res.body.aviso).toMatch(/RUT no proporcionado/i)
    expect(keycloakService.crearUsuario).not.toHaveBeenCalled()
  })

  test('error en Keycloak retorna 201 con aviso (paciente ya fue creado en MS)', async () => {
    service.crearPaciente.mockResolvedValue({ data: { id: 3, nombre: 'Laura' } })
    keycloakService.crearUsuario.mockRejectedValue({
      message: 'Connection refused',
      response: { data: { errorMessage: 'User exists' } }
    })

    const res = await request(app)
      .post('/admin/pacientes')
      .send({ rut: '11111111-1', nombre: 'Laura' })

    expect(res.status).toBe(201)
    expect(res.body.aviso).toMatch(/No se pudo crear el usuario en Keycloak/i)
  })

  test('retorna 400 si los datos del paciente son inválidos', async () => {
    service.crearPaciente.mockRejectedValue({
      response: { status: 400, data: 'RUT inválido' }
    })

    const res = await request(app).post('/admin/pacientes').send({ rut: 'invalido' })

    expect(res.status).toBe(400)
    expect(res.body.error).toBe('Datos inválidos')
  })

  test('retorna 409 si ya existe un conflicto en el sistema', async () => {
    service.crearPaciente.mockRejectedValue({ response: { status: 409 } })

    const res = await request(app).post('/admin/pacientes').send({ rut: '12345678-9' })

    expect(res.status).toBe(409)
  })

  test('retorna 503 si el servicio del MS no está disponible', async () => {
    service.crearPaciente.mockRejectedValue({ response: { status: 503 } })

    const res = await request(app).post('/admin/pacientes').send({ rut: '12345678-9' })

    expect(res.status).toBe(503)
  })
})
