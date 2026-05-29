const { handleGatewayError } = require('../utils/gatewayError')

let res

beforeEach(() => {
  res = {
    status: jest.fn().mockReturnThis(),
    json: jest.fn()
  }
})

describe('handleGatewayError — errores conocidos del gateway', () => {
  test.each([
    [401, 'Token ausente, inválido o expirado.'],
    [403, 'No tienes permisos para realizar esta acción.'],
    [429, 'Demasiadas solicitudes. Intenta de nuevo en unos momentos.'],
    [503, 'Servicio no disponible. El servicio está temporalmente fuera de línea.'],
    [504, 'Tiempo de espera agotado. El servidor tardó demasiado en responder.'],
  ])('error %i responde con su mensaje específico', (status, message) => {
    handleGatewayError(res, { response: { status } }, 'fallback')

    expect(res.status).toHaveBeenCalledWith(status)
    expect(res.json).toHaveBeenCalledWith({ error: message })
  })
})

describe('handleGatewayError — errores no conocidos', () => {
  test('código HTTP no mapeado responde 500 con el mensaje fallback', () => {
    handleGatewayError(res, { response: { status: 422 } }, 'Error genérico')

    expect(res.status).toHaveBeenCalledWith(500)
    expect(res.json).toHaveBeenCalledWith({ error: 'Error genérico' })
  })

  test('error sin response responde 500 con el mensaje fallback', () => {
    handleGatewayError(res, {}, 'Error de red')

    expect(res.status).toHaveBeenCalledWith(500)
    expect(res.json).toHaveBeenCalledWith({ error: 'Error de red' })
  })

})
