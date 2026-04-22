const express = require('express')
const cors = require('cors')
const app = express()

app.use(cors())
app.use(express.json())

// Rutas
const portalRoutes = require('./routes/portal')
const adminRoutes = require('./routes/admin')

app.use('/portal', portalRoutes)
app.use('/admin', adminRoutes)

app.get('/health', (req, res) => {
  res.json({ status: 'BFF funcionando' })
})

app.listen(3001, () => {
  console.log('BFF corriendo en puerto 3001')
})