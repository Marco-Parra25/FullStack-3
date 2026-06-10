const router = require('express').Router()
const service = require('../services/notificacionesService')
const { handleGatewayError } = require('../utils/gatewayError')

/**
 * @swagger
 * tags:
 *   name: Notificaciones
 *   description: Envío y consulta de notificaciones
 */

/**
 * @swagger
 * /notificaciones/enviar:
 *   post:
 *     summary: Enviar una notificación a un paciente
 *     tags: [Notificaciones]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [destinatario, mensaje]
 *             properties:
 *               destinatario:
 *                 type: string
 *                 example: juan.perez@mail.com
 *               mensaje:
 *                 type: string
 *                 example: Su cita ha sido confirmada para el 15/07/2026.
 *               tipo:
 *                 type: string
 *                 example: EMAIL
 *     responses:
 *       200:
 *         description: Notificación enviada correctamente
 *       500:
 *         description: Error al enviar notificación
 */
router.post('/enviar', async (req, res) => {
  try {
    const resultado = await service.enviarNotificacion(req.body)
    res.json(resultado.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al enviar notificación')
  }
})

/**
 * @swagger
 * /notificaciones/historial:
 *   get:
 *     summary: Obtener historial de notificaciones enviadas
 *     tags: [Notificaciones]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Array con el historial de notificaciones
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 type: object
 *       500:
 *         description: Error al obtener historial
 */
router.get('/historial', async (req, res) => {
  try {
    const historial = await service.obtenerHistorial()
    res.json(historial.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al obtener historial')
  }
})

/**
 * @swagger
 * /notificaciones/campania:
 *   post:
 *     summary: Crear una campaña de notificaciones masivas
 *     tags: [Notificaciones]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [nombre, mensaje]
 *             properties:
 *               nombre:
 *                 type: string
 *                 example: Campaña vacunación 2026
 *               mensaje:
 *                 type: string
 *                 example: Recuerde su cita de vacunación programada.
 *               especialidad:
 *                 type: string
 *                 example: Medicina General
 *     responses:
 *       200:
 *         description: Campaña creada correctamente
 *       500:
 *         description: Error al crear campaña
 */
router.post('/campania', async (req, res) => {
  try {
    const resultado = await service.crearCampania(req.body)
    res.json(resultado.data)
  } catch (error) {
    handleGatewayError(res, error, 'Error al crear campaña')
  }
})

module.exports = router
