import { useEffect, useState } from 'react'
import axiosInstance from '../services/axiosInstance'
import Footer from '../components/Footer'

interface Ficha {
  id: number
  pacienteNombre: string
  pacienteRut: string
  tipoAtencion: string
  especialidad: string
  prioridad: number
  estado: string
  fechaIngreso: string
}

function getRutFromToken(): string | null {
  const token = sessionStorage.getItem('token')
  if (!token) return null
  try {
    const parts = token.split('.')
    if (parts.length === 3) {
      const decoded = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
      return decoded.preferred_username ?? decoded.sub ?? null
    } else {
      const decoded = JSON.parse(atob(token))
      return decoded.sub ?? null
    }
  } catch {
    return null
  }
}

export default function PortalPacientes() {
  const [ficha, setFicha] = useState<Ficha | null>(null)
  const [totalEnEspera, setTotalEnEspera] = useState<number>(0)
  const [pacientesEnEspecialidad, setPacientesEnEspecialidad] = useState<number>(0)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const rut = getRutFromToken()
    if (!rut) {
      setError('No se pudo obtener el RUT desde la sesión. Intenta iniciar sesión nuevamente.')
      setLoading(false)
      return
    }

    axiosInstance.get(`/portal/rut/${rut}`)
      .then(res => {
        const { ficha: fichaData, totalEnEspera: total } = res.data
        if (!fichaData) {
          setError('No tienes citas registradas en lista de espera actualmente.')
          return Promise.resolve(null)
        }
        setFicha(fichaData)
        setTotalEnEspera(total)
        return Promise.all([
          axiosInstance.get(`/portal/especialidad/${fichaData.especialidad}`)
        ])
      })
      .then(results => {
        if (!results) return
        const [especialidadRes] = results
        setPacientesEnEspecialidad(especialidadRes.data.length)
      })
      .catch(() => setError('No se pudo cargar tu información. Intenta más tarde.'))
      .finally(() => setLoading(false))
  }, [])

  return (
    <>
      <div className="page">
        <h1>Portal Pacientes — RedNorte</h1>

        {loading && <p>Cargando...</p>}

        {error && !loading && (
          <p className="portal-error">{error}</p>
        )}

        {ficha && (
          <div className="card portal-ficha">
            <h2>Mi estado en lista de espera</h2>
            <p><strong>Nombre:</strong> {ficha.pacienteNombre}</p>
            <p><strong>RUT:</strong> {ficha.pacienteRut}</p>
            <p><strong>Especialidad:</strong> {ficha.especialidad}</p>
            <p><strong>Tipo atención:</strong> {ficha.tipoAtencion}</p>
            <p><strong>Prioridad:</strong> {ficha.prioridad}</p>
            <p><strong>Estado:</strong> <span className="badge-espera">{ficha.estado}</span></p>
            <p><strong>Fecha ingreso:</strong> {ficha.fechaIngreso}</p>
            <hr className="portal-divider" />
            <p><strong>Total pacientes en espera:</strong> {totalEnEspera}</p>
            <p><strong>Pacientes en tu especialidad:</strong> {pacientesEnEspecialidad}</p>
          </div>
        )}
      </div>

      <Footer />
    </>
  )
}
