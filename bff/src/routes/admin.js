const router = require('express').Router()
const service = require('../services/listaEsperaService')
const keycloakService = require('../services/keycloakService')
const { handleGatewayError } = require('../utils/gatewayError')

/**
 * @swagger
 * tags:
 *   name: Admin
 *   description: Gestión de lista de espera y pacientes (requiere rol administrador)
 */

/**
 * @swagger
 * /admin/lista:
 *   get:
 *     summary: Obtener lista de espera ordenada por prioridad
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de pacientes en espera con total
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 pacientes:
 *                   type: array
 *                   items:
 *                     type: object
 *                 totalEnEspera:
 *                   type: integer
 *                   example: 12
 *       500:
 *         description: Error al obtener la lista
 */
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

/**
 * @swagger
 * /admin/registrar:
 *   post:
 *     summary: Registrar un paciente en la lista de espera
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [pacienteRut, especialidad, prioridad]
 *             properties:
 *               pacienteRut:
 *                 type: string
 *                 example: 12345678-9
 *               especialidad:
 *                 type: string
 *                 example: Cardiología
 *               prioridad:
 *                 type: integer
 *                 example: 1
 *     responses:
 *       201:
 *         description: Paciente registrado en la lista de espera
 *       404:
 *         description: Paciente no encontrado
 *       500:
 *         description: Error al registrar
 */
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

/**
 * @swagger
 * /admin/cancelar/{id}:
 *   patch:
 *     summary: Cancelar una entrada de la lista de espera
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: ID de la entrada en lista de espera
 *     responses:
 *       200:
 *         description: Cita cancelada y reasignación automática iniciada
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 mensaje:
 *                   type: string
 *                   example: Cita cancelada. El sistema reasignará automáticamente y notificará al paciente.
 *       500:
 *         description: Error al cancelar
 */
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

/**
 * @swagger
 * /admin/estado/{id}:
 *   patch:
 *     summary: Actualizar el estado de una entrada en lista de espera
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: ID de la entrada en lista de espera
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [estado]
 *             properties:
 *               estado:
 *                 type: string
 *                 example: ATENDIDO
 *     responses:
 *       200:
 *         description: Estado actualizado correctamente
 *       500:
 *         description: Error al actualizar estado
 */
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

/**
 * @swagger
 * /admin/pacientes:
 *   get:
 *     summary: Listar todos los pacientes
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Array de pacientes
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 type: object
 *       500:
 *         description: Error al obtener pacientes
 */
router.get('/pacientes', async (req, res) => {
  try {
    const pacientes = await service.listarPacientes(req.headers.authorization)
    res.json(pacientes.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener pacientes')
  }
})

/**
 * @swagger
 * /admin/pacientes:
 *   post:
 *     summary: Crear un nuevo paciente y su usuario en Keycloak
 *     tags: [Admin]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [rut, nombre, apellido]
 *             properties:
 *               rut:
 *                 type: string
 *                 example: 12345678-9
 *               nombre:
 *                 type: string
 *                 example: Juan
 *               apellido:
 *                 type: string
 *                 example: Pérez
 *               email:
 *                 type: string
 *                 example: juan.perez@mail.com
 *     responses:
 *       201:
 *         description: Paciente creado. Si Keycloak falla, se incluye un campo `aviso`.
 *       400:
 *         description: Datos inválidos
 *       409:
 *         description: Usuario Keycloak ya existe
 *       500:
 *         description: Error al crear paciente
 */
router.post('/pacientes', async (req, res) => {
  try {
    const paciente = await service.crearPaciente(req.body, req.headers.authorization)
    const rut = req.body.rut

    if (!rut) {
      return res.status(201).json({ ...paciente.data, aviso: 'Paciente creado. No se pudo crear el usuario en Keycloak: RUT no proporcionado' })
    }

    const password = rut.replace(/[.\-]/g, '')

    try {
      const createRes = await keycloakService.crearUsuario({
        username: rut,
        enabled: true,
        emailVerified: true,
        requiredActions: [],
        firstName: req.body.nombre || paciente.data.nombre || '',
        lastName: req.body.apellido || paciente.data.apellido || '',
        email: req.body.email || '',
        credentials: [{ type: 'password', value: password, temporary: false }]
      })

      const location = createRes.headers?.location || ''
      const userId = location.split('/').pop()
      console.log('userId extraído:', userId)
      await keycloakService.asignarRol(userId, 'PACIENTE')
      await keycloakService.asignarRol(userId, 'USER')
      console.log('Limpiando acciones requeridas para userId:', userId)
      await keycloakService.limpiarAccionesRequeridas(userId)
      console.log('Acciones limpiadas correctamente')
    } catch (keycloakError) {
      const detalle = keycloakError.response?.data?.errorMessage || keycloakError.message || 'error desconocido'
      console.error('Keycloak error detalle:', JSON.stringify(keycloakError.response?.data), keycloakError.message)
      return res.status(201).json({ ...paciente.data, aviso: `Paciente creado. No se pudo crear el usuario en Keycloak: ${detalle}` })
    }

    res.status(201).json(paciente.data)
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
