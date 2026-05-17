import { Link, useNavigate } from 'react-router-dom'

interface NavbarProps {
  rol: string
  setRol: (r: string) => void
}

const NOMBRE_ROL: Record<string, string> = {
  admin: 'Administrativo',
  medico: 'Médico',
  paciente: 'Paciente',
}

export default function Navbar({ rol, setRol }: NavbarProps) {
  const navigate = useNavigate()

  const cerrarSesion = () => {
    sessionStorage.removeItem('token')
    setRol('')
    navigate('/')
  }

  return (
    <nav>
      <div className="nav-left">
        <span className="nav-brand">RedNorte</span>

        {rol === 'admin' && (
          <>
            <Link to="/admin">Panel Admin</Link>
            <Link to="/pacientes">Gestión Pacientes</Link>
          </>
        )}
        {rol === 'medico' && (
          <Link to="/dashboard">Dashboard</Link>
        )}
        {rol === 'paciente' && (
          <Link to="/portal">Portal</Link>
        )}
      </div>

      <div className="nav-right">
        <span className="nav-role">{NOMBRE_ROL[rol] ?? rol}</span>
        <button className="nav-logout" onClick={cerrarSesion}>
          Cerrar sesión
        </button>
      </div>
    </nav>
  )
}
