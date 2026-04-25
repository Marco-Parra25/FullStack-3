import { useEffect, useState } from 'react'
import axios from 'axios'

const BFF_URL = 'http://localhost:3001'

interface Paciente {
  id: number
  pacienteNombre: string
  tipoAtencion: string
  especialidad: string
  prioridad: number
  estado: string
  fechaIngreso: string
}

export default function Dashboard() {
  const [pacientes, setPacientes] = useState<Paciente[]>([])
  const [totalEnEspera, setTotalEnEspera] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    axios.get(`${BFF_URL}/admin/lista`)
      .then(res => {
        setPacientes(res.data.pacientes)
        setTotalEnEspera(res.data.totalEnEspera)
        setLoading(false)
      })
      .catch(() => setLoading(false))

    const intervalo = setInterval(() => {
      axios.get(`${BFF_URL}/admin/lista`)
        .then(res => {
          setPacientes(res.data.pacientes)
          setTotalEnEspera(res.data.totalEnEspera)
        })
    }, 30000)

    return () => clearInterval(intervalo)
  }, [])

  const porEspecialidad = pacientes.reduce((acc, p) => {
    acc[p.especialidad] = (acc[p.especialidad] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  const porTipo = pacientes.reduce((acc, p) => {
    acc[p.tipoAtencion] = (acc[p.tipoAtencion] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  const porPrioridad = pacientes.reduce((acc, p) => {
    const label = p.prioridad === 1 ? 'Alta' : p.prioridad === 2 ? 'Media' : 'Normal'
    acc[label] = (acc[label] || 0) + 1
    return acc
  }, {} as Record<string, number>)

  if (loading) return <p>Cargando...</p>

  return (
    <div className="page">
      <h1>Dashboard Médico — RedNorte</h1>
      <p style={{ color: '#888', fontSize: '13px', marginBottom: '1.5rem' }}>
        Actualización automática cada 30 segundos
      </p>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem', marginBottom: '2rem' }}>
        <div className="card" style={{ textAlign: 'center' }}>
          <p style={{ fontSize: '40px', fontWeight: 700, color: '#0B1F3A' }}>{totalEnEspera}</p>
          <p style={{ color: '#888' }}>Total en espera</p>
        </div>
        <div className="card" style={{ textAlign: 'center' }}>
          <p style={{ fontSize: '40px', fontWeight: 700, color: '#0D9488' }}>
            {Object.keys(porEspecialidad).length}
          </p>
          <p style={{ color: '#888' }}>Especialidades activas</p>
        </div>
        <div className="card" style={{ textAlign: 'center' }}>
          <p style={{ fontSize: '40px', fontWeight: 700, color: '#E24B4A' }}>
            {porPrioridad['Alta'] || 0}
          </p>
          <p style={{ color: '#888' }}>Prioridad alta</p>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>

        <div className="card">
          <h2>Pacientes por especialidad</h2>
          <table style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Especialidad</th>
                <th>Cantidad</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(porEspecialidad).map(([esp, count]) => (
                <tr key={esp}>
                  <td>{esp}</td>
                  <td><span className="stat-badge">{count}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="card">
          <h2>Pacientes por tipo de atención</h2>
          <table style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Tipo</th>
                <th>Cantidad</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(porTipo).map(([tipo, count]) => (
                <tr key={tipo}>
                  <td>{tipo}</td>
                  <td><span className="stat-badge">{count}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="card">
          <h2>Pacientes por prioridad</h2>
          <table style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Prioridad</th>
                <th>Cantidad</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(porPrioridad).map(([prioridad, count]) => (
                <tr key={prioridad}>
                  <td>{prioridad}</td>
                  <td><span className="stat-badge">{count}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="card">
          <h2>Últimos ingresos</h2>
          <table style={{ marginTop: '1rem' }}>
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Especialidad</th>
                <th>Fecha</th>
              </tr>
            </thead>
            <tbody>
              {pacientes.slice(0, 5).map(p => (
                <tr key={p.id}>
                  <td>{p.pacienteNombre}</td>
                  <td>{p.especialidad}</td>
                  <td>{p.fechaIngreso}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

      </div>
    </div>
  )
}