const router = require('express').Router()
const axios = require('axios')
const qs = require('querystring')

/**
 * @swagger
 * tags:
 *   name: Auth
 *   description: Autenticación mediante Keycloak
 */

/**
 * @swagger
 * /auth/login:
 *   post:
 *     summary: Iniciar sesión
 *     tags: [Auth]
 *     security: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required: [username, password]
 *             properties:
 *               username:
 *                 type: string
 *                 example: admin
 *               password:
 *                 type: string
 *                 example: secreto123
 *     responses:
 *       200:
 *         description: Login exitoso, retorna token JWT
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 token:
 *                   type: string
 *                   example: eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
 *       401:
 *         description: Credenciales inválidas
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 error:
 *                   type: string
 *                   example: Credenciales inválidas
 */
router.post('/login', async (req, res) => {
  try {
    const { username, password } = req.body
    const response = await axios.post(
      `${process.env.KEYCLOAK_URL}/realms/${process.env.KEYCLOAK_REALM}/protocol/openid-connect/token`,
      qs.stringify({
        grant_type: 'password',
        client_id: process.env.KEYCLOAK_CLIENT_ID,
        client_secret: process.env.KEYCLOAK_CLIENT_SECRET,
        username,
        password
      }),
      { headers: { 'Content-Type': 'application/x-www-form-urlencoded' } }
    )
    res.json({ token: response.data.access_token })
  } catch (error) {
    console.error('Login error:', error.response?.data)
    res.status(401).json({ error: 'Credenciales inválidas' })
  }
})

module.exports = router
