const axios = require('axios')

const GATEWAY_URL = process.env.API_GATEWAY_URL || process.env.GATEWAY_URL || 'http://localhost:8080'
const BASE_URL = `${GATEWAY_URL}/api/lista-espera/api/v1`

const authConfig = (authorization) => ({
  headers: authorization ? { Authorization: authorization } : {}
})

module.exports = {
  // Lista de espera
  listarTodos: (authorization) =>
    axios.get(`${BASE_URL}/waitlist`, authConfig(authorization)),

  obtenerPorId: (id, authorization) =>
    axios.get(`${BASE_URL}/waitlist/${id}`, authConfig(authorization)),

  listarPorPrioridad: (authorization) =>
    axios.get(`${BASE_URL}/waitlist/prioridad`, authConfig(authorization)),

  listarPorEspecialidad: (especialidad, authorization) =>
    axios.get(`${BASE_URL}/waitlist/especialidad/${especialidad}`, authConfig(authorization)),

  registrar: (data, authorization) =>
    axios.post(`${BASE_URL}/waitlist`, data, authConfig(authorization)),

  cancelar: (id, authorization) =>
    axios.patch(`${BASE_URL}/waitlist/${id}/cancelar`, undefined, authConfig(authorization)),

  actualizarEstado: (id, estado, authorization) =>
    axios.patch(`${BASE_URL}/waitlist/${id}/estado`, { estado }, authConfig(authorization)),

  contarEnEspera: (authorization) =>
    axios.get(`${BASE_URL}/waitlist/count`, authConfig(authorization)),

  // Pacientes
  listarPacientes: (authorization) =>
    axios.get(`${BASE_URL}/pacientes`, authConfig(authorization)),

  crearPaciente: (data, authorization) =>
    axios.post(`${BASE_URL}/pacientes`, data, authConfig(authorization)),

  obtenerPacientePorId: (id, authorization) =>
    axios.get(`${BASE_URL}/pacientes/${id}`, authConfig(authorization)),
}
