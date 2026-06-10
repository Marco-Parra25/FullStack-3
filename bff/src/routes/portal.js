const router = require('express').Router()
const service = require('../services/listaEsperaService')
const { handleGatewayError } = require('../utils/gatewayError')

/**
 * @swagger
 * tags:
 *   name: Portal
 *   description: Consultas del portal de pacientes
 */

/**
 * @swagger
 * /portal/estado/{id}:
 *   get:
 *     summary: Obtener ficha de lista de espera por ID
 *     tags: [Portal]
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
 *         description: Ficha del paciente y total en espera
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 ficha:
 *                   type: object
 *                 totalEnEspera:
 *                   type: integer
 *                   example: 12
 *       500:
 *         description: Error al obtener estado
 */
router.get('/estado/:id', async (req, res) => {
  try {
    const authorization = req.headers.authorization
    const ficha = await service.obtenerPorId(req.params.id, authorization)
    const count = await service.contarEnEspera(authorization)
    res.json({
      ficha: ficha.data,
      totalEnEspera: count.data.enEspera
    })
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener estado')
  }
})

/**
 * @swagger
 * /portal/especialidad/{especialidad}:
 *   get:
 *     summary: Obtener lista de espera filtrada por especialidad
 *     tags: [Portal]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: especialidad
 *         required: true
 *         schema:
 *           type: string
 *         description: Nombre de la especialidad médica
 *         example: Cardiología
 *     responses:
 *       200:
 *         description: Array de pacientes en espera para esa especialidad
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 type: object
 *       500:
 *         description: Error al obtener lista
 */
router.get('/especialidad/:especialidad', async (req, res) => {
  try {
    const lista = await service.listarTodos(req.headers.authorization)
    const filtrada = lista.data.filter(
      p => p.especialidad.toLowerCase() === req.params.especialidad.toLowerCase()
    )
    res.json(filtrada)
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener lista')
  }
})

/**
 * @swagger
 * /portal/rut/{rut}:
 *   get:
 *     summary: Buscar posición y ficha de un paciente por RUT
 *     tags: [Portal]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: rut
 *         required: true
 *         schema:
 *           type: string
 *         description: RUT del paciente
 *         example: 12345678-9
 *     responses:
 *       200:
 *         description: Ficha, posición en lista, total en espera y cantidad en la especialidad
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 ficha:
 *                   type: object
 *                   nullable: true
 *                 posicionEnEspera:
 *                   type: integer
 *                   nullable: true
 *                   example: 3
 *                 totalEnEspera:
 *                   type: integer
 *                   example: 12
 *                 pacientesEnEspecialidad:
 *                   type: integer
 *                   example: 5
 *       500:
 *         description: Error al buscar
 */
router.get('/rut/:rut', async (req, res) => {
  try {
    const authorization = req.headers.authorization
    const [listaRes, countRes] = await Promise.all([
      service.listarTodos(authorization),
      service.contarEnEspera(authorization)
    ])
    const lista = listaRes.data
    const ficha = lista.find(p => p.pacienteRut === req.params.rut) || null
    const listaOrdenada = [...lista].sort((a, b) => a.prioridad - b.prioridad)
    const posicion = listaOrdenada.findIndex(p => p.pacienteRut === req.params.rut)
    const posicionEnEspera = posicion !== -1 ? posicion + 1 : null
    const totalEnEspera = countRes.data.enEspera
    const pacientesEnEspecialidad = ficha
      ? lista.filter(p => p.especialidad.toLowerCase() === ficha.especialidad.toLowerCase()).length
      : 0
    res.json({ ficha, posicionEnEspera, totalEnEspera, pacientesEnEspecialidad })
  } catch (error) {
    handleGatewayError(res, error, 'Error al buscar')
  }
})

module.exports = router
