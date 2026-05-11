import { useEffect, useState } from 'react'
import axiosInstance from '../services/axiosInstance'

interface Paciente {
  id: number
  pacienteNombre: string
  pacienteRut: string
  tipoAtencion: string
  especialidad: string
  prioridad: number
  estado: string
  fechaIngreso: string
}

interface PacienteDisponible {
  id: number
  nombre: string
  apellido: string
  rut: string
}

export default function PanelAdmin() {
  const [pacientes, setPacientes] = useState<Paciente[]>([])
  const [totalEnEspera, setTotalEnEspera] = useState<number>(0)
  const [loading, setLoading] = useState(true)
  const [pacientesDisponibles, setPacientesDisponibles] = useState<PacienteDisponible[]>([])

  const [form, setForm] = useState({
    pacienteId: '',
    tipoAtencion: 'CONSULTA',
    especialidad: ''
  })

  const cargarLista = () => {
    axiosInstance.get('/api/bff/admin/lista')
      .then(res => {
        setPacientes(res.data.pacientes)
        setTotalEnEspera(res.data.totalEnEspera)
        setPacientesDisponibles(res.data.pacientesDisponibles ?? [])
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }

useEffect(() => {
  cargarLista()

  const intervalo = setInterval(() => {
    cargarLista()
  }, 30000)

  return () => clearInterval(intervalo)
}, [])

  const registrar = () => {
    if (!form.pacienteId || !form.especialidad) {
      alert('Completa todos los campos')
      return
    }
    axiosInstance.post('/api/bff/admin/registrar', {
      pacienteId: parseInt(form.pacienteId),
      tipoAtencion: form.tipoAtencion,
      especialidad: form.especialidad
    })
      .then(() => {
        alert('Paciente registrado exitosamente')
        setForm({ pacienteId: '', tipoAtencion: 'CONSULTA', especialidad: '' })
        cargarLista()
      })
      .catch((error) => {
        if (error.response?.status === 404) {
          alert('Paciente no encontrado en el sistema')
        } else {
          alert('Error al registrar paciente')
        }
      })
  }

  const cancelar = (id: number) => {
    if (!confirm(`¿Cancelar cita del paciente ${id}?`)) return
    axiosInstance.patch(`/api/bff/admin/cancelar/${id}`)
      .then(() => {
        alert('Cita cancelada')
        cargarLista()
      })
      .catch(() => alert('Error al cancelar'))
  }

  const cambiarEstado = (id: number, estado: string) => {
    if (!confirm(`¿Cambiar estado del paciente ${id} a "${estado}"?`)) return
    axiosInstance.patch(`/api/bff/admin/waitlist/${id}/estado`, { estado })
      .then(() => {
        cargarLista()
      })
      .catch(() => alert('Error al actualizar estado'))
  }

  if (loading) return <p>Cargando...</p>

  return (
    <div className="page">
      <h1>Panel Administrativo — RedNorte</h1>
      <span className="stat-badge">Total en espera: {totalEnEspera}</span>

      <h2>Registrar nuevo paciente</h2>
      <div className="form-row">
        <select
          title="Seleccionar paciente"
          value={form.pacienteId}
          onChange={e => setForm({ ...form, pacienteId: e.target.value })}
        >
          <option value="">Seleccionar paciente</option>
          {pacientesDisponibles.map(p => (
            <option key={p.id} value={p.id}>
              {p.id} - {p.nombre} {p.apellido}
            </option>
          ))}
        </select>

        <select
          title="Tipo de atención"
          value={form.tipoAtencion}
          onChange={e => setForm({ ...form, tipoAtencion: e.target.value })}
        >
          <option value="CONSULTA">Consulta</option>
          <option value="CIRUGIA">Cirugía</option>
          <option value="URGENCIA_DIFERIDA">Urgencia diferida</option>
        </select>

        <input
          type="text"
          placeholder="Especialidad"
          value={form.especialidad}
          onChange={e => setForm({ ...form, especialidad: e.target.value })}
        />

        <button type="button" className="btn-primary" onClick={registrar}>
          Registrar
        </button>
      </div>

      <h2>Lista de espera</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>RUT</th>
            <th>Tipo</th>
            <th>Especialidad</th>
            <th>Prioridad</th>
            <th>Estado</th>
            <th>Fecha ingreso</th>
            <th>Acción</th>
          </tr>
        </thead>
        <tbody>
          {pacientes.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.pacienteNombre}</td>
              <td>{p.pacienteRut}</td>
              <td>{p.tipoAtencion}</td>
              <td>{p.especialidad}</td>
              <td>{p.prioridad}</td>
              <td><span className="badge-espera">{p.estado}</span></td>
              <td>{p.fechaIngreso}</td>
              <td style={{ display: 'flex', gap: '0.4rem', flexWrap: 'wrap' }}>
                <button type="button" className="btn-primary" onClick={() => cambiarEstado(p.id, 'EN_ATENCION')}>
                  En atención
                </button>
                <button type="button" className="btn-primary" onClick={() => cambiarEstado(p.id, 'ATENDIDO')}>
                  Atendido
                </button>
                <button type="button" className="btn-danger" onClick={() => cancelar(p.id)}>
                  Cancelar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}