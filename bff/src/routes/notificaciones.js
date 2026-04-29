const router = require('express').Router()
const service = require('../services/notificacionesService')

router.post('/enviar', async (req, res) => {
  try {
    const resultado = await service.enviarNotificacion(req.body)
    res.json(resultado.data)
  } catch (error) {
    res.status(500).json({ error: 'Error al enviar notificación' })
  }
})

router.get('/historial', async (req, res) => {
  try {
    const historial = await service.obtenerHistorial()
    res.json(historial.data)
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener historial' })
  }
})

router.post('/campania', async (req, res) => {
  try {
    const resultado = await service.crearCampania(req.body)
    res.json(resultado.data)
  } catch (error) {
    res.status(500).json({ error: 'Error al crear campaña' })
  }
})

module.exports = router
