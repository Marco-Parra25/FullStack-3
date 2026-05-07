const router = require('express').Router()
const service = require('../services/listaEsperaService')
const { handleGatewayError } = require('../utils/gatewayError')

// Lista de espera
router.get('/lista', async (req, res) => {
  try {
    const [lista, count, pacientes] = await Promise.all([
      service.listarPorPrioridad(),
      service.contarEnEspera(),
      service.listarPacientes(),
    ])
    res.json({
      pacientes: lista.data,
      totalEnEspera: count.data.enEspera,
      pacientesDisponibles: pacientes.data,
    })
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener lista')
  }
})

router.post('/registrar', async (req, res) => {
  try {
    const resultado = await service.registrar(req.body)
    res.status(201).json(resultado.data)
  } catch (error) {
    if (error.response?.status === 404) {
      return res.status(404).json({ error: 'Paciente no encontrado' })
    }
    handleGatewayError(res, error, 'Error al registrar')
  }
})

router.patch('/cancelar/:id', async (req, res) => {
  try {
    await service.cancelar(req.params.id)
    res.status(204).send()
  } catch (error) {
    handleGatewayError(res, error, 'Error al cancelar')
  }
})

router.patch('/waitlist/:id/estado', async (req, res) => {
  try {
    const resultado = await service.actualizarEstado(req.params.id, req.body.estado)
    res.json(resultado.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al actualizar estado')
  }
})

// Pacientes
router.get('/pacientes', async (req, res) => {
  try {
    const pacientes = await service.listarPacientes()
    res.json(pacientes.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener pacientes')
  }
})

router.post('/pacientes', async (req, res) => {
  try {
    const paciente = await service.crearPaciente(req.body)
    res.status(201).json(paciente.data)
  } catch (error) {
    if (error.response?.status === 400) {
      return res.status(400).json({ error: 'Datos inválidos', detalle: error.response.data })
    }
    handleGatewayError(res, error, 'Error al crear paciente')
  }
})

module.exports = router
