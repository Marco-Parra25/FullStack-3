const axios = require('axios')

const GATEWAY_URL = process.env.GATEWAY_URL || 'http://localhost:8080'
const BASE_URL = `${GATEWAY_URL}/api/notificaciones/api/v1`

module.exports = {
  enviarNotificacion: (data) =>
    axios.post(`${BASE_URL}/notificaciones/enviar`, data),

  obtenerHistorial: () =>
    axios.get(`${BASE_URL}/notificaciones/historial`),

  crearCampania: (data) =>
    axios.post(`${BASE_URL}/notificaciones/campania`, data),
}
