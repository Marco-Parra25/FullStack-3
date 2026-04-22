import { useEffect, useState } from 'react'
import axios from 'axios'

const BFF_URL = 'http://localhost:3001'

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
    axios.get(`${BFF_URL}/admin/lista`)
      .then(res => {
        setPacientes(res.data.pacientes)
        setTotalEnEspera(res.data.totalEnEspera)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }

  const cargarPacientes = () => {
    axios.get(`${BFF_URL}/admin/pacientes`)
      .then(res => setPacientesDisponibles(res.data))
      .catch(() => console.error('Error al cargar pacientes'))
  }

useEffect(() => {
  cargarLista()
  cargarPacientes()

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
    axios.post(`${BFF_URL}/admin/registrar`, {
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
    axios.patch(`${BFF_URL}/admin/cancelar/${id}`)
      .then(() => {
        alert('Cita cancelada')
        cargarLista()
      })
      .catch(() => alert('Error al cancelar'))
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

        <button className="btn-primary" onClick={registrar}>
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
              <td>
                <button className="btn-danger" onClick={() => cancelar(p.id)}>
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