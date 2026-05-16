const router = require('express').Router()
const crypto = require('crypto')
const service = require('../services/listaEsperaService')
const keycloakService = require('../services/keycloakService')
const { handleGatewayError } = require('../utils/gatewayError')

const generarClaveTemporal = () =>
  crypto.randomBytes(6).toString('base64url').slice(0, 10)

// Lista de espera
router.get('/lista', async (req, res) => {
  try {
    const authorization = req.headers.authorization
    const lista = await service.listarPorPrioridad(authorization)
    const count = await service.contarEnEspera(authorization)
    res.json({
      pacientes: lista.data,
      totalEnEspera: count.data.enEspera
    })
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener lista')
  }
})

router.post('/registrar', async (req, res) => {
  try {
    const resultado = await service.registrar(req.body, req.headers.authorization)
    res.status(201).json(resultado.data)
  } catch (error) {
    if (error.response?.status === 404) {
      return res.status(404).json({ error: 'Paciente no encontrado' })
    }
    handleGatewayError(res, error, 'Error al registrar')
  }
})

router.patch('/cancelar/:id', async (req, res) => {
  const id = req.params.id
  const authorization = req.headers.authorization

  try {
    await service.cancelar(id, authorization)
    res.json({ mensaje: 'Cita cancelada. El sistema reasignará automáticamente y notificará al paciente.' })
  } catch (error) {
    console.error('Error en PATCH /cancelar/:id:', error)
    handleGatewayError(res, error, 'Error al cancelar')
  }
})

router.patch('/estado/:id', async (req, res) => {
  try {
    const resultado = await service.actualizarEstado(
      req.params.id,
      req.body.estado,
      req.headers.authorization
    )
    res.json(resultado.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al actualizar estado')
  }
})

// Pacientes
router.get('/pacientes', async (req, res) => {
  try {
    const pacientes = await service.listarPacientes(req.headers.authorization)
    res.json(pacientes.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener pacientes')
  }
})

router.post('/pacientes', async (req, res) => {
  try {
    const paciente = await service.crearPaciente(req.body, req.headers.authorization)
    const rut = req.body.rut || paciente.data.rut

    if (!rut) {
      return res.status(400).json({ error: 'RUT requerido para crear usuario en Keycloak' })
    }

    const claveTemporal = generarClaveTemporal()
    await keycloakService.crearUsuario({
      username: rut,
      enabled: true,
      firstName: req.body.nombre || paciente.data.nombre || '',
      lastName: req.body.apellido || paciente.data.apellido || '',
      credentials: [
        {
          type: 'password',
          value: claveTemporal,
          temporary: true
        }
      ]
    })

    res.status(201).json({ ...paciente.data, claveTemporal })
  } catch (error) {
    if (error.response?.status === 400) {
      return res.status(400).json({ error: 'Datos inválidos', detalle: error.response.data })
    }
    if (error.response?.status === 409) {
      return res.status(409).json({ error: 'Usuario Keycloak ya existe' })
    }
    handleGatewayError(res, error, 'Error al crear paciente')
  }
})

module.exports = router
