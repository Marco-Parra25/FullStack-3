const axios = require('axios')

jest.mock('axios')

const service = require('../services/listaEsperaService')

const TOKEN = 'Bearer test-token'

beforeEach(() => jest.clearAllMocks())

// ─── authConfig: las dos ramas del header de autorización ─────────────────────

describe('listaEsperaService — header de autorización', () => {
  test('incluye Authorization cuando se pasa un token', async () => {
    axios.get.mockResolvedValue({ data: [] })
    await service.listarTodos(TOKEN)

    expect(axios.get).toHaveBeenCalledWith(
      expect.any(String),
      { headers: { Authorization: TOKEN } }
    )
  })

  test('omite Authorization cuando no se pasa token', async () => {
    axios.get.mockResolvedValue({ data: [] })
    await service.listarTodos(undefined)

    expect(axios.get).toHaveBeenCalledWith(
      expect.any(String),
      { headers: {} }
    )
  })
})

// ─── Lista de espera ──────────────────────────────────────────────────────────

describe('listaEsperaService.listarTodos', () => {
  test('llama GET /waitlist y retorna la respuesta', async () => {
    axios.get.mockResolvedValue({ data: [{ id: 1 }] })

    const res = await service.listarTodos(TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/waitlist'), expect.any(Object))
    expect(res.data).toHaveLength(1)
  })
})

describe('listaEsperaService.obtenerPorId', () => {
  test('llama GET /waitlist/:id con el id correcto', async () => {
    axios.get.mockResolvedValue({ data: { id: 5 } })

    const res = await service.obtenerPorId(5, TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/waitlist/5'), expect.any(Object))
    expect(res.data.id).toBe(5)
  })
})

describe('listaEsperaService.listarPorPrioridad', () => {
  test('llama GET /waitlist/prioridad', async () => {
    axios.get.mockResolvedValue({ data: [] })

    await service.listarPorPrioridad(TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/waitlist/prioridad'), expect.any(Object))
  })
})

describe('listaEsperaService.listarPorEspecialidad', () => {
  test('llama GET /waitlist/especialidad/:esp con la especialidad codificada', async () => {
    axios.get.mockResolvedValue({ data: [] })

    await service.listarPorEspecialidad('Cardiología', TOKEN)

    expect(axios.get).toHaveBeenCalledWith(
      expect.stringContaining('/waitlist/especialidad/Cardiología'),
      expect.any(Object)
    )
  })
})

describe('listaEsperaService.registrar', () => {
  test('llama POST /waitlist con el body y el token', async () => {
    axios.post.mockResolvedValue({ data: { id: 1 } })
    const body = { pacienteRut: '12345678-9', especialidad: 'Cardiología' }

    const res = await service.registrar(body, TOKEN)

    expect(axios.post).toHaveBeenCalledWith(
      expect.stringContaining('/waitlist'),
      body,
      { headers: { Authorization: TOKEN } }
    )
    expect(res.data.id).toBe(1)
  })
})

describe('listaEsperaService.cancelar', () => {
  test('llama PATCH /waitlist/:id/cancelar', async () => {
    axios.patch.mockResolvedValue({ data: {} })

    await service.cancelar(3, TOKEN)

    expect(axios.patch).toHaveBeenCalledWith(
      expect.stringContaining('/waitlist/3/cancelar'),
      undefined,
      { headers: { Authorization: TOKEN } }
    )
  })
})

describe('listaEsperaService.actualizarEstado', () => {
  test('llama PATCH /waitlist/:id/estado con el estado en el body', async () => {
    axios.patch.mockResolvedValue({ data: { estado: 'ATENDIDO' } })

    await service.actualizarEstado(2, 'ATENDIDO', TOKEN)

    expect(axios.patch).toHaveBeenCalledWith(
      expect.stringContaining('/waitlist/2/estado'),
      { estado: 'ATENDIDO' },
      { headers: { Authorization: TOKEN } }
    )
  })
})

describe('listaEsperaService.contarEnEspera', () => {
  test('llama GET /waitlist/count', async () => {
    axios.get.mockResolvedValue({ data: { enEspera: 10 } })

    const res = await service.contarEnEspera(TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/waitlist/count'), expect.any(Object))
    expect(res.data.enEspera).toBe(10)
  })
})

// ─── Pacientes ────────────────────────────────────────────────────────────────

describe('listaEsperaService.listarPacientes', () => {
  test('llama GET /pacientes', async () => {
    axios.get.mockResolvedValue({ data: [{ id: 1, nombre: 'Ana' }] })

    const res = await service.listarPacientes(TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/pacientes'), expect.any(Object))
    expect(res.data).toHaveLength(1)
  })
})

describe('listaEsperaService.crearPaciente', () => {
  test('llama POST /pacientes con el body y el token', async () => {
    axios.post.mockResolvedValue({ data: { id: 7, nombre: 'Luis' } })
    const body = { nombre: 'Luis', rut: '99999999-9' }

    const res = await service.crearPaciente(body, TOKEN)

    expect(axios.post).toHaveBeenCalledWith(
      expect.stringContaining('/pacientes'),
      body,
      { headers: { Authorization: TOKEN } }
    )
    expect(res.data.id).toBe(7)
  })
})

describe('listaEsperaService.obtenerPacientePorId', () => {
  test('llama GET /pacientes/:id con el id correcto', async () => {
    axios.get.mockResolvedValue({ data: { id: 4, nombre: 'Marta' } })

    const res = await service.obtenerPacientePorId(4, TOKEN)

    expect(axios.get).toHaveBeenCalledWith(expect.stringContaining('/pacientes/4'), expect.any(Object))
    expect(res.data.nombre).toBe('Marta')
  })
})

// ─── Propagación de errores ───────────────────────────────────────────────────

describe('listaEsperaService — propagación de errores', () => {
  test('listarTodos propaga el error de axios', async () => {
    axios.get.mockRejectedValue(new Error('Network error'))

    await expect(service.listarTodos(TOKEN)).rejects.toThrow('Network error')
  })

  test('registrar propaga el error de axios', async () => {
    axios.post.mockRejectedValue(new Error('Conflict'))

    await expect(service.registrar({}, TOKEN)).rejects.toThrow('Conflict')
  })
})
