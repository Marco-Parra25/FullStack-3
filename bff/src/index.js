require('dotenv').config()
const express = require('express')
const cors = require('cors')
const swaggerUi = require('swagger-ui-express')
const swaggerJsdoc = require('swagger-jsdoc')
const app = express()

const swaggerOptions = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'PurebaFS BFF API',
      version: '1.0.0',
      description: 'Backend For Frontend del sistema de lista de espera hospitalaria'
    },
    servers: [{ url: 'http://localhost:3001' }],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT'
        }
      }
    },
    security: [{ bearerAuth: [] }]
  },
  apis: ['./src/routes/*.js', './src/index.js']
}

const swaggerSpec = swaggerJsdoc(swaggerOptions)

app.use(cors())
app.use(express.json())

app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec))

// Rutas
const portalRoutes = require('./routes/portal')
const adminRoutes = require('./routes/admin')
const notificacionesRoutes = require('./routes/notificaciones')
const authRoutes = require('./routes/auth')

app.use('/portal', portalRoutes)
app.use('/admin', adminRoutes)
app.use('/notificaciones', notificacionesRoutes)
app.use('/auth', authRoutes)

/**
 * @swagger
 * /health:
 *   get:
 *     summary: Verificar estado del BFF
 *     tags: [Health]
 *     security: []
 *     responses:
 *       200:
 *         description: BFF funcionando correctamente
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 status:
 *                   type: string
 *                   example: BFF funcionando
 */
app.get('/health', (req, res) => {
  res.json({ status: 'BFF funcionando' })
})

const PORT = process.env.PORT || 3001
app.listen(PORT, () => {
  console.log(`BFF corriendo en puerto ${PORT}`)
})
