require('dotenv').config()
const express = require('express')
const cors = require('cors')
const app = express()

app.use(cors())
app.use(express.json())

// Rutas
const portalRoutes = require('./routes/portal')
const adminRoutes = require('./routes/admin')
const notificacionesRoutes = require('./routes/notificaciones')

app.use('/portal', portalRoutes)
app.use('/admin', adminRoutes)
app.use('/notificaciones', notificacionesRoutes)

app.get('/health', (req, res) => {
  res.json({ status: 'BFF funcionando' })
})

const PORT = process.env.PORT || 3001
app.listen(PORT, () => {
  console.log(`BFF corriendo en puerto ${PORT}`)
})