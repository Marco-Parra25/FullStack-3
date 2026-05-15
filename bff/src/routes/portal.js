const router = require('express').Router()
const service = require('../services/listaEsperaService')
const { handleGatewayError } = require('../utils/gatewayError')

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

router.get('/rut/:rut', async (req, res) => {
  try {
    const authorization = req.headers.authorization
    const lista = await service.listarTodos(authorization)
    const ficha = lista.data.find(
      p => p.pacienteRut === req.params.rut
    )
    const count = await service.contarEnEspera(authorization)
    res.json({
      ficha: ficha || null,
      totalEnEspera: count.data.enEspera
    })
  } catch (error) {
    handleGatewayError(res, error, 'Error al buscar')
  }
})

module.exports = router
