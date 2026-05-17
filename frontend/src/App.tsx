import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { useState } from 'react'
import PortalPacientes from './pages/PortalPacientes'
import PanelAdmin from './pages/PanelAdmin'
import GestionPacientes from './pages/GestionPacientes'
import Dashboard from './pages/Dashboard'
import Login from './pages/Login'
import Navbar from './components/Navbar'

function App() {
  const [rol, setRol] = useState('')

  return (
    <BrowserRouter>
      {rol && <Navbar rol={rol} setRol={setRol} />}
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