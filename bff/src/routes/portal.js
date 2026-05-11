const router = require('express').Router()
const service = require('../services/listaEsperaService')
const { handleGatewayError } = require('../utils/gatewayError')

router.get('/estado/:id', async (req, res) => {
  try {
    const ficha = await service.obtenerPorId(req.params.id)
    const count = await service.contarEnEspera()
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
    const lista = await service.listarTodos()
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
    const [lista, count] = await Promise.all([
      service.listarTodos(),
      service.contarEnEspera(),
    ])

    const ficha = lista.data.find(p => p.pacienteRut === req.params.rut) || null

    // Personas con mayor prioridad (número menor) o mismo nivel pero ingresadas antes
    const posicionEnCola = ficha
      ? lista.data.filter(p =>
          p.estado === 'EN_ESPERA' &&
          (p.prioridad < ficha.prioridad ||
            (p.prioridad === ficha.prioridad && p.id < ficha.id))
        ).length
      : 0

    res.json({
      ficha,
      totalEnEspera: count.data.enEspera,
      posicionEnCola,
    })
  } catch (error) {
    handleGatewayError(res, error, 'Error al buscar')
  }
})

module.exports = router
