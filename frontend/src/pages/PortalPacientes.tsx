import { useState } from 'react'
import axios from 'axios'

const BFF_URL = 'http://localhost:3001'

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

export default function PortalPacientes() {
  const [ficha, setFicha] = useState<Ficha | null>(null)
  const [totalEnEspera, setTotalEnEspera] = useState<number>(0)
  const [loading, setLoading] = useState(false)
  const [busquedaId, setBusquedaId] = useState('')
  const [buscado, setBuscado] = useState(false)

  const buscarPaciente = () => {
    if (!busquedaId) return
    setLoading(true)
    setBuscado(false)
    axios.get(`${BFF_URL}/portal/rut/${busquedaId}`)
      .then(res => {
        setFicha(res.data.ficha)
        setTotalEnEspera(res.data.totalEnEspera)
        setLoading(false)
        setBuscado(true)
      })
      .catch(() => {
        setFicha(null)
        setLoading(false)
        setBuscado(true)
      })
  }

  return (
    <div className="page">
      <h1>Portal Pacientes — RedNorte</h1>

      <div className="form-row" style={{ marginTop: '1rem' }}>
        <input
          type="text"
          placeholder="Ingresa tu RUT (ej: 12345678-9)"
          value={busquedaId}
          onChange={e => setBusquedaId(e.target.value)}
          style={{ width: '250px' }}
        />
        <button className="btn-primary" onClick={buscarPaciente}>
          Buscar
        </button>
      </div>

      {loading && <p>Cargando...</p>}

      {ficha && (
        <div className="card" style={{ marginTop: '1.5rem', maxWidth: '500px' }}>
          <h2>Mi estado en lista de espera</h2>
          <p><strong>Nombre:</strong> {ficha.pacienteNombre}</p>
          <p><strong>RUT:</strong> {ficha.pacienteRut}</p>
          <p><strong>Especialidad:</strong> {ficha.especialidad}</p>
          <p><strong>Tipo atención:</strong> {ficha.tipoAtencion}</p>
          <p><strong>Prioridad:</strong> {ficha.prioridad}</p>
          <p><strong>Estado:</strong> <span className="badge-espera">{ficha.estado}</span></p>
          <p><strong>Fecha ingreso:</strong> {ficha.fechaIngreso}</p>
          <hr style={{ margin: '1rem 0', borderColor: '#e8edf2' }} />
          <p><strong>Total pacientes en espera:</strong> {totalEnEspera}</p>
        </div>
      )}

      {!loading && !ficha && buscado && (
        <p style={{ marginTop: '1rem', color: '#E24B4A' }}>
          No se encontró información para ese RUT
        </p>
      )}
    </div>
  )
}