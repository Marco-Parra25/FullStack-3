import { BrowserRouter, Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import PortalPacientes from './pages/PortalPacientes'
import PanelAdmin from './pages/PanelAdmin'
import GestionPacientes from './pages/GestionPacientes'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import NoAutorizado from './pages/NoAutorizado'

function getRolFromToken(): string {
  const token = sessionStorage.getItem('token')
  if (!token) return ''
  try {
    if (token.includes('.')) {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')))
      const roles: string[] = payload.realm_access?.roles ?? []
      if (roles.includes('admin')) return 'admin'
      if (roles.includes('medico')) return 'medico'
      return 'paciente'
    }
    const payload = JSON.parse(atob(token))
    return payload.rol || ''
  } catch {
    return ''
  }
}

function ProtectedRoute({
  rol,
  rolesPermitidos,
  children,
}: {
  rol: string
  rolesPermitidos: string[]
  children: React.ReactNode
}) {
  const token = sessionStorage.getItem('token')
  if (!token || !rol) return <Navigate to="/" replace />
  if (!rolesPermitidos.includes(rol)) return <Navigate to="/403" replace />
  return <>{children}</>
}

function NavBar({ rol, setRol }: { rol: string, setRol: (r: string) => void }) {
  const navigate = useNavigate()

  const cerrarSesion = () => {
    sessionStorage.clear()
    setRol('')
    navigate('/', { replace: true })
  }

  const nombreRol = rol === 'admin' ? 'Administrativo'
    : rol === 'medico' ? 'Médico'
    : 'Paciente'

  return (
    <nav>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <span style={{ color: '#0D9488', fontWeight: 700, fontSize: '16px' }}>
          RedNorte
        </span>

        {rol === 'paciente' && (
          <Link to="/portal">Portal Pacientes</Link>
        )}
        {rol === 'admin' && (
          <>
            <Link to="/admin">Panel Admin</Link>
            <Link to="/pacientes">Gestión Pacientes</Link>
          </>
        )}
        {rol === 'medico' && (
          <Link to="/dashboard">Dashboard</Link>
        )}
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <span style={{ color: '#9FE1CB', fontSize: '13px' }}>
          {nombreRol}
        </span>
        <button
          onClick={cerrarSesion}
          style={{
            padding: '0.3rem 0.8rem',
            background: 'transparent',
            color: 'white',
            border: '1px solid #0D9488',
            borderRadius: '6px',
            fontSize: '13px',
            cursor: 'pointer'
          }}
        >
          Cerrar sesión
        </button>
      </div>
    </nav>
  )
}

function App() {
  const [rol, setRol] = useState(() => getRolFromToken())

  return (
    <BrowserRouter>
      {rol && <NavBar rol={rol} setRol={setRol} />}
      <Routes>
        <Route path="/" element={
          rol ? (
            rol === 'admin' ? <Navigate to="/admin" replace />
            : rol === 'medico' ? <Navigate to="/dashboard" replace />
            : <Navigate to="/portal" replace />
          ) : <Login setRol={setRol} />
        } />
        <Route path="/portal" element={
          <ProtectedRoute rol={rol} rolesPermitidos={['paciente']}>
            <PortalPacientes />
          </ProtectedRoute>
        } />
        <Route path="/admin" element={
          <ProtectedRoute rol={rol} rolesPermitidos={['admin']}>
            <PanelAdmin />
          </ProtectedRoute>
        } />
        <Route path="/pacientes" element={
          <ProtectedRoute rol={rol} rolesPermitidos={['admin']}>
            <GestionPacientes />
          </ProtectedRoute>
        } />
        <Route path="/dashboard" element={
          <ProtectedRoute rol={rol} rolesPermitidos={['medico', 'admin']}>
            <Dashboard />
          </ProtectedRoute>
        } />
        <Route path="/403" element={<NoAutorizado />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App