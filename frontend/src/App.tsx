import { BrowserRouter, Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import PortalPacientes from './pages/PortalPacientes'
import PanelAdmin from './pages/PanelAdmin'
import GestionPacientes from './pages/GestionPacientes'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'

function NavBar({ rol, setRol }: { rol: string, setRol: (r: string) => void }) {
  const navigate = useNavigate()

  const cerrarSesion = () => {
    setRol('')
    navigate('/')
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
  const [rol, setRol] = useState('')

  return (
    <BrowserRouter>
      {rol && <NavBar rol={rol} setRol={setRol} />}
      <Routes>
        <Route path="/" element={
          rol ? (
            rol === 'admin' ? <Navigate to="/admin" />
            : rol === 'medico' ? <Navigate to="/dashboard" />
            : <Navigate to="/portal" />
          ) : <Login setRol={setRol} />
        } />
        <Route path="/portal" element={rol ? <PortalPacientes /> : <Navigate to="/" />} />
        <Route path="/admin" element={rol ? <PanelAdmin /> : <Navigate to="/" />} />
        <Route path="/pacientes" element={rol ? <GestionPacientes /> : <Navigate to="/" />} />
        <Route path="/dashboard" element={rol ? <Dashboard /> : <Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App