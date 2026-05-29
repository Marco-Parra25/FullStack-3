const request = require('supertest')
const express = require('express')

jest.mock('../services/notificacionesService')

const service = require('../services/notificacionesService')
const notificacionesRouter = require('../routes/notificaciones')

const app = express()
app.use(express.json())
app.use('/notificaciones', notificacionesRouter)

beforeEach(() => jest.clearAllMocks())

describe('POST /notificaciones/enviar', () => {
  test('envía notificación y retorna la respuesta del servicio', async () => {
    service.enviarNotificacion.mockResolvedValue({ data: { enviado: true } })

    const res = await request(app)
      .post('/notificaciones/enviar')
      .send({ destinatario: 'paciente@test.com', mensaje: 'Cupo disponible' })

    expect(res.status).toBe(200)
    expect(res.body.enviado).toBe(true)
  })

  test('retorna 401 si el token es inválido', async () => {
    service.enviarNotificacion.mockRejectedValue({ response: { status: 401 } })

    const res = await request(app)
      .post('/notificaciones/enviar')
      .send({ destinatario: 'paciente@test.com' })

    expect(res.status).toBe(401)
  })

  test('retorna 500 si el servicio falla', async () => {
    service.enviarNotificacion.mockRejectedValue({})

    const res = await request(app).post('/notificaciones/enviar').send({})

    expect(res.status).toBe(500)
    expect(res.body.error).toBe('Error al enviar notificación')
  })
})

describe('GET /notificaciones/historial', () => {
  test('retorna el historial de notificaciones', async () => {
    service.obtenerHistorial.mockResolvedValue({
      data: [{ id: 1, mensaje: 'Notificación enviada' }]
    })

    const res = await request(app).get('/notificaciones/historial')

    expect(res.status).toBe(200)
    expect(res.body).toHaveLength(1)
  })

  test('retorna 503 si el servicio no está disponible', async () => {
    service.obtenerHistorial.mockRejectedValue({ response: { status: 503 } })

    const res = await request(app).get('/notificaciones/historial')

    expect(res.status).toBe(503)
  })

  test('retorna 500 si falla sin código conocido', async () => {
    service.obtenerHistorial.mockRejectedValue({})

    const res = await request(app).get('/notificaciones/historial')

    expect(res.status).toBe(500)
  })
})

describe('POST /notificaciones/campania', () => {
  test('crea una campaña y retorna la respuesta del servicio', async () => {
    service.crearCampania.mockResolvedValue({ data: { id: 10, nombre: 'Campaña Test' } })

    const res = await request(app)
      .post('/notificaciones/campania')
      .send({ nombre: 'Campaña Test', especialidad: 'Cardiología' })

    expect(res.status).toBe(200)
    expect(res.body.id).toBe(10)
  })

  test('retorna 403 si no tiene permisos', async () => {
    service.crearCampania.mockRejectedValue({ response: { status: 403 } })

    const res = await request(app).post('/notificaciones/campania').send({})

    expect(res.status).toBe(403)
  })

  test('retorna 500 si falla sin código conocido', async () => {
    service.crearCampania.mockRejectedValue({})

    const res = await request(app).post('/notificaciones/campania').send({})

    expect(res.status).toBe(500)
  })
})
