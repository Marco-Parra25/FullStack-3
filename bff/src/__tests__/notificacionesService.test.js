const axios = require('axios')

jest.mock('axios')

const service = require('../services/notificacionesService')

beforeEach(() => jest.clearAllMocks())

describe('notificacionesService.enviarNotificacion', () => {
  test('llama POST al endpoint correcto con el body recibido', async () => {
    axios.post.mockResolvedValue({ data: { enviado: true } })
    const body = { destinatario: 'paciente@test.com', mensaje: 'Cupo disponible' }

    const res = await service.enviarNotificacion(body)

    expect(axios.post).toHaveBeenCalledWith(
      expect.stringContaining('/notificaciones/enviar'),
      body
    )
    expect(res.data.enviado).toBe(true)
  })

  test('propaga el error si axios falla', async () => {
    axios.post.mockRejectedValue(new Error('Network error'))

    await expect(service.enviarNotificacion({})).rejects.toThrow('Network error')
  })
})

describe('notificacionesService.obtenerHistorial', () => {
  test('llama GET al endpoint de historial', async () => {
    axios.get.mockResolvedValue({ data: [{ id: 1 }, { id: 2 }] })

    const res = await service.obtenerHistorial()

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/notificaciones/historial'))
    expect(res.data).toHaveLength(2)
  })

  test('propaga el error si axios falla', async () => {
    axios.get.mockRejectedValue(new Error('Timeout'))

    await expect(service.obtenerHistorial()).rejects.toThrow('Timeout')
  })
})

describe('notificacionesService.crearCampania', () => {
  test('llama POST al endpoint de campaña con el body recibido', async () => {
    axios.post.mockResolvedValue({ data: { id: 10, nombre: 'Campaña A' } })
    const body = { nombre: 'Campaña A', especialidad: 'Cardiología' }

    const res = await service.crearCampania(body)

    expect(axios.post).toHaveBeenCalledWith(
      expect.stringContaining('/notificaciones/campania'),
      body
    )
    expect(res.data.id).toBe(10)
  })

  test('propaga el error si axios falla', async () => {
    axios.post.mockRejectedValue(new Error('Service unavailable'))

    await expect(service.crearCampania({})).rejects.toThrow('Service unavailable')
  })
})
