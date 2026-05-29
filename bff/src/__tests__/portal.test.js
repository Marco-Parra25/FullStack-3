const request = require('supertest')
const express = require('express')

jest.mock('../services/listaEsperaService')

const service = require('../services/listaEsperaService')
const portalRouter = require('../routes/portal')

const app = express()
app.use(express.json())
app.use('/portal', portalRouter)

const MOCK_LISTA = [
  { id: 1, pacienteRut: '11111111-1', especialidad: 'Cardiología', prioridad: 2, estado: 'EN_ESPERA', pacienteNombre: 'Juan' },
  { id: 2, pacienteRut: '22222222-2', especialidad: 'Cardiología', prioridad: 1, estado: 'EN_ESPERA', pacienteNombre: 'Ana' },
  { id: 3, pacienteRut: '33333333-3', especialidad: 'Neurología',  prioridad: 3, estado: 'EN_ESPERA', pacienteNombre: 'Pedro' },
]

beforeEach(() => jest.clearAllMocks())

describe('GET /portal/estado/:id', () => {
  test('retorna ficha y total en espera', async () => {
    service.obtenerPorId.mockResolvedValue({ data: MOCK_LISTA[0] })
    service.contarEnEspera.mockResolvedValue({ data: { enEspera: 3 } })

    const res = await request(app)
      .get('/portal/estado/1')
      .set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body.ficha.pacienteRut).toBe('11111111-1')
    expect(res.body.totalEnEspera).toBe(3)
  })

  test('retorna 401 si el token es inválido', async () => {
    service.obtenerPorId.mockRejectedValue({ response: { status: 401 } })

    const res = await request(app).get('/portal/estado/1')

    expect(res.status).toBe(401)
  })

  test('retorna 500 si el servicio falla', async () => {
    service.obtenerPorId.mockRejectedValue({})

    const res = await request(app).get('/portal/estado/1')

    expect(res.status).toBe(500)
  })
})

describe('GET /portal/especialidad/:especialidad', () => {
  test('retorna solo los pacientes de la especialidad indicada', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })

    const res = await request(app)
      .get('/portal/especialidad/Cardiología')
      .set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body).toHaveLength(2)
    res.body.forEach(p => expect(p.especialidad).toBe('Cardiología'))
  })

  test('filtro es case-insensitive', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })

    const res = await request(app).get('/portal/especialidad/cardiología')

    expect(res.status).toBe(200)
    expect(res.body).toHaveLength(2)
  })

  test('retorna lista vacía si no hay pacientes en la especialidad', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })

    const res = await request(app).get('/portal/especialidad/Dermatología')

    expect(res.status).toBe(200)
    expect(res.body).toHaveLength(0)
  })

  test('retorna 503 si el servicio no está disponible', async () => {
    service.listarTodos.mockRejectedValue({ response: { status: 503 } })

    const res = await request(app).get('/portal/especialidad/Cardiología')

    expect(res.status).toBe(503)
  })
})

describe('GET /portal/rut/:rut', () => {
  test('retorna ficha con posición, total y pacientes en especialidad cuando el RUT existe', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })
    service.contarEnEspera.mockResolvedValue({ data: { enEspera: 3 } })

    const res = await request(app)
      .get('/portal/rut/11111111-1')
      .set('Authorization', 'Bearer token')

    expect(res.status).toBe(200)
    expect(res.body.ficha.pacienteRut).toBe('11111111-1')
    // lista ordenada por prioridad: [22222222-2 (1), 11111111-1 (2), 33333333-3 (3)]
    // posición de 11111111-1 es índice 1 → posicionEnEspera = 2
    expect(res.body.posicionEnEspera).toBe(2)
    expect(res.body.totalEnEspera).toBe(3)
    // dos pacientes con Cardiología
    expect(res.body.pacientesEnEspecialidad).toBe(2)
  })

  test('ficha nula y posición nula si el RUT no está en la lista', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })
    service.contarEnEspera.mockResolvedValue({ data: { enEspera: 3 } })

    const res = await request(app).get('/portal/rut/99999999-9')

    expect(res.status).toBe(200)
    expect(res.body.ficha).toBeNull()
    expect(res.body.posicionEnEspera).toBeNull()
    expect(res.body.pacientesEnEspecialidad).toBe(0)
    expect(res.body.totalEnEspera).toBe(3)
  })

  test('paciente con menor prioridad numérica aparece en posición 1', async () => {
    service.listarTodos.mockResolvedValue({ data: MOCK_LISTA })
    service.contarEnEspera.mockResolvedValue({ data: { enEspera: 3 } })

    const res = await request(app).get('/portal/rut/22222222-2')

    expect(res.status).toBe(200)
    expect(res.body.posicionEnEspera).toBe(1)
  })

  test('retorna 504 si el gateway agota el tiempo', async () => {
    service.listarTodos.mockRejectedValue({ response: { status: 504 } })

    const res = await request(app).get('/portal/rut/11111111-1')

    expect(res.status).toBe(504)
  })

  test('retorna 500 si el servicio falla sin código conocido', async () => {
    service.listarTodos.mockRejectedValue({})

    const res = await request(app).get('/portal/rut/11111111-1')

    expect(res.status).toBe(500)
  })
})
