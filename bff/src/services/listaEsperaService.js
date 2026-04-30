const axios = require('axios')

const GATEWAY_URL = process.env.API_GATEWAY_URL || process.env.GATEWAY_URL || 'http://ms-api-gateway:8080'
const BASE_URL = `${GATEWAY_URL}/api/v1`

module.exports = {
  // Lista de espera
  listarTodos: () =>
    axios.get(`${BASE_URL}/waitlist`),

  obtenerPorId: (id) =>
    axios.get(`${BASE_URL}/waitlist/${id}`),

  listarPorPrioridad: () =>
    axios.get(`${BASE_URL}/waitlist/prioridad`),

  listarPorEspecialidad: (especialidad) =>
    axios.get(`${BASE_URL}/waitlist/especialidad/${especialidad}`),

  registrar: (data) =>
    axios.post(`${BASE_URL}/waitlist`, data),

  cancelar: (id) =>
    axios.patch(`${BASE_URL}/waitlist/${id}/cancelar`),

  contarEnEspera: () =>
    axios.get(`${BASE_URL}/waitlist/count`),

  // Pacientes
  listarPacientes: () =>
    axios.get(`${BASE_URL}/pacientes`),

  crearPaciente: (data) =>
    axios.post(`${BASE_URL}/pacientes`, data),

  obtenerPacientePorId: (id) =>
    axios.get(`${BASE_URL}/pacientes/${id}`),
}
