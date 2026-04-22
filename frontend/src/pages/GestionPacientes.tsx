import { useEffect, useState } from 'react'
import axios from 'axios'

const BFF_URL = 'http://localhost:3001'

interface Paciente {
  id: number
  rut: string
  nombre: string
  apellido: string
  telefono: string
  email: string
}

export default function GestionPacientes() {
  const [pacientes, setPacientes] = useState<Paciente[]>([])
  const [filtro, setFiltro] = useState('')
  const [loading, setLoading] = useState(true)

  const [form, setForm] = useState({
    rut: '',
    nombre: '',
    apellido: '',
    telefono: '',
    email: ''
  })

  const cargarPacientes = () => {
    axios.get(`${BFF_URL}/admin/pacientes`)
      .then(res => {
        setPacientes(res.data)
        setLoading(false)
      })
      .catch(() => setLoading(false))
  }

  useEffect(() => {
    cargarPacientes()
  }, [])

  const crearPaciente = () => {
    if (!form.rut || !form.nombre || !form.apellido) {
      alert('RUT, nombre y apellido son obligatorios')
      return
    }
    axios.post(`${BFF_URL}/admin/pacientes`, form)
      .then(res => {
        alert(`Paciente ${res.data.nombre} creado con ID ${res.data.id}`)
        setForm({ rut: '', nombre: '', apellido: '', telefono: '', email: '' })
        cargarPacientes()
      })
      .catch(error => {
        if (error.response?.status === 400) {
          alert('Datos inválidos. Verifica el formato del RUT y email')
        } else {
          alert('Error al crear paciente')
        }
      })
  }

  const pacientesFiltrados = pacientes.filter(p =>
    p.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    p.apellido.toLowerCase().includes(filtro.toLowerCase()) ||
    p.rut.includes(filtro)
  )

  if (loading) return <p>Cargando...</p>

  return (
    <div className="page">
      <h1>Gestión de Pacientes — RedNorte</h1>
      <span className="stat-badge">Total pacientes: {pacientes.length}</span>

      <h2>Registrar nuevo paciente</h2>
      <div className="form-row">
        <input
          type="text"
          placeholder="RUT (ej: 12345678-9)"
          value={form.rut}
          onChange={e => setForm({ ...form, rut: e.target.value })}
        />
        <input
          type="text"
          placeholder="Nombre"
          value={form.nombre}
          onChange={e => setForm({ ...form, nombre: e.target.value })}
        />
        <input
          type="text"
          placeholder="Apellido"
          value={form.apellido}
          onChange={e => setForm({ ...form, apellido: e.target.value })}
        />
        <input
          type="text"
          placeholder="Teléfono (+56912345678)"
          value={form.telefono}
          onChange={e => setForm({ ...form, telefono: e.target.value })}
        />
        <input
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={e => setForm({ ...form, email: e.target.value })}
        />
        <button className="btn-primary" onClick={crearPaciente}>
          Crear paciente
        </button>
      </div>
      <p className="hint">* RUT formato: 12345678-9 | Teléfono formato: +56912345678</p>

      <h2>Lista de pacientes</h2>
      <div className="form-row" style={{ marginBottom: '1rem' }}>
        <input
          type="text"
          placeholder="Filtrar por nombre, apellido o RUT"
          value={filtro}
          onChange={e => setFiltro(e.target.value)}
          style={{ width: '300px' }}
        />
      </div>

      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>RUT</th>
            <th>Nombre</th>
            <th>Apellido</th>
            <th>Teléfono</th>
            <th>Email</th>
          </tr>
        </thead>
        <tbody>
          {pacientesFiltrados.map(p => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.rut}</td>
              <td>{p.nombre}</td>
              <td>{p.apellido}</td>
              <td>{p.telefono || '-'}</td>
              <td>{p.email || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {pacientesFiltrados.length === 0 && (
        <p style={{ marginTop: '1rem', color: '#888' }}>
          No se encontraron pacientes con ese filtro
        </p>
      )}
    </div>
  )
}