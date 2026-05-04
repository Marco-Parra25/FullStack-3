import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

interface Props {
  setRol: (rol: string) => void
}

const MOCK_USERS = [
  { usuario: 'admin', password: '1234', rol: 'admin', nombre: 'María Pérez' },
  { usuario: 'medico', password: '1234', rol: 'medico', nombre: 'Dr. Carlos López' },
  { usuario: 'paciente', password: '1234', rol: 'paciente', nombre: 'Juan González' },
]

function decodeJwtPayload(token: string): Record<string, unknown> {
  const payload = token.split('.')[1]
  const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
  return JSON.parse(decoded)
}

function extractRole(payload: Record<string, unknown>): string {
  const realmAccess = payload.realm_access as { roles?: string[] } | undefined
  const roles = realmAccess?.roles ?? []
  if (roles.includes('admin')) return 'admin'
  if (roles.includes('medico')) return 'medico'
  return 'paciente'
}

export default function Login({ setRol }: Props) {
  const [usuario, setUsuario] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const useMock = import.meta.env.VITE_USE_MOCK_AUTH === 'true'

  const loginMock = () => {
    const user = MOCK_USERS.find(u => u.usuario === usuario && u.password === password)
    if (user) {
      const mockToken = btoa(JSON.stringify({ sub: user.usuario, rol: user.rol, nombre: user.nombre }))
      sessionStorage.setItem('token', mockToken)
      setRol(user.rol)
      if (user.rol === 'admin') navigate('/admin')
      else if (user.rol === 'medico') navigate('/dashboard')
      else navigate('/portal')
    } else {
      setError('Usuario o contraseña incorrectos')
    }
  }

  const loginKeycloak = async () => {
    setError('')
    setLoading(true)
    try {
      const keycloakUrl = import.meta.env.VITE_KEYCLOAK_URL
      const clientId = import.meta.env.VITE_KEYCLOAK_CLIENT_ID
      const res = await fetch(
        `${keycloakUrl}/realms/rednorte/protocol/openid-connect/token`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: new URLSearchParams({
            grant_type: 'password',
            client_id: clientId,
            username: usuario,
            password,
          }),
        }
      )
      if (!res.ok) {
        setError('Usuario o contraseña incorrectos')
        return
      }
      const data = await res.json()
      sessionStorage.setItem('token', data.access_token)
      const payload = decodeJwtPayload(data.access_token)
      const rol = extractRole(payload)
      setRol(rol)
      if (rol === 'admin') navigate('/admin')
      else if (rol === 'medico') navigate('/dashboard')
      else navigate('/portal')
    } catch {
      setError('No se pudo conectar con el servidor de autenticación')
    } finally {
      setLoading(false)
    }
  }

  const login = () => {
    if (useMock) loginMock()
    else loginKeycloak()
  }

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#f0f4f8'
    }}>
      <div className="card" style={{ width: '380px', padding: '2.5rem' }}>
        <h1 style={{ textAlign: 'center', marginBottom: '0.3rem' }}>RedNorte</h1>
        <p style={{ textAlign: 'center', color: '#888', marginBottom: '2rem', fontSize: '14px' }}>
          Sistema de Gestión de Listas de Espera
        </p>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ fontSize: '13px', color: '#444', display: 'block', marginBottom: '0.3rem' }}>
            Usuario
          </label>
          <input
            type="text"
            placeholder="Ingresa tu usuario"
            value={usuario}
            onChange={e => setUsuario(e.target.value)}
            style={{ width: '100%', padding: '0.6rem', borderRadius: '6px', border: '1px solid #ccd6e0', fontSize: '14px' }}
          />
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{ fontSize: '13px', color: '#444', display: 'block', marginBottom: '0.3rem' }}>
            Contraseña
          </label>
          <input
            type="password"
            placeholder="Ingresa tu contraseña"
            value={password}
            onChange={e => setPassword(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && login()}
            style={{ width: '100%', padding: '0.6rem', borderRadius: '6px', border: '1px solid #ccd6e0', fontSize: '14px' }}
          />
        </div>

        {error && (
          <p style={{ color: '#E24B4A', fontSize: '13px', marginBottom: '1rem' }}>{error}</p>
        )}

        <button
          type="button"
          className="btn-primary"
          onClick={login}
          disabled={loading}
          style={{ width: '100%', padding: '0.7rem', opacity: loading ? 0.7 : 1 }}
        >
          {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
        </button>

        {useMock && (
          <div style={{ marginTop: '1.5rem', padding: '1rem', background: '#f5f9fc', borderRadius: '6px' }}>
            <p style={{ fontSize: '12px', color: '#888', marginBottom: '0.5rem' }}>Usuarios de prueba:</p>
            <p style={{ fontSize: '12px', color: '#555' }}>admin / 1234 → Administrativo</p>
            <p style={{ fontSize: '12px', color: '#555' }}>medico / 1234 → Médico</p>
            <p style={{ fontSize: '12px', color: '#555' }}>paciente / 1234 → Paciente</p>
          </div>
        )}
      </div>
    </div>
  )
}
