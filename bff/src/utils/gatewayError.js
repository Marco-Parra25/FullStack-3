const GATEWAY_ERRORS = {
  429: { status: 429, error: 'Demasiadas solicitudes. Intenta de nuevo en unos momentos.' },
  503: { status: 503, error: 'Servicio no disponible. El servicio está temporalmente fuera de línea.' },
  504: { status: 504, error: 'Tiempo de espera agotado. El servidor tardó demasiado en responder.' },
}

/**
 * Responde con el error correcto según el código HTTP del gateway.
 * Si no es un error conocido del gateway, devuelve el fallback indicado.
 * @param {import('express').Response} res
 * @param {any} error
 * @param {string} fallbackMessage
 */
function handleGatewayError(res, error, fallbackMessage) {
  const gatewayError = GATEWAY_ERRORS[error.response?.status]
  if (gatewayError) {
    return res.status(gatewayError.status).json({ error: gatewayError.error })
  }
  res.status(500).json({ error: fallbackMessage })
}

module.exports = { handleGatewayError }
