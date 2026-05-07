import { useNavigate } from 'react-router-dom'

export default function NoAutorizado() {
  const navigate = useNavigate()

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: '#f0f4f8'
    }}>
      <div className="card" style={{ width: '420px', padding: '2.5rem', textAlign: 'center' }}>
        <p style={{ fontSize: '64px', fontWeight: 700, color: '#E24B4A', margin: 0 }}>403</p>
        <h1 style={{ marginTop: '0.5rem', marginBottom: '0.75rem' }}>Acceso denegado</h1>
        <p style={{ color: '#888', fontSize: '14px', marginBottom: '2rem' }}>
          No tienes permiso para acceder a esta página.
        </p>
        <button
          type="button"
          className="btn-primary"
          onClick={() => {
            sessionStorage.clear()
            navigate('/', { replace: true })
          }}
          style={{ padding: '0.7rem 2rem' }}
        >
          Volver al inicio
        </button>
      </div>
    </div>
  )
}
