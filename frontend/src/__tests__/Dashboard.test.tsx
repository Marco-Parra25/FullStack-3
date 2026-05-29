import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import axiosInstance from '../services/axiosInstance'
import Dashboard from '../pages/Dashboard'

vi.mock('../services/axiosInstance', () => ({
  default: { get: vi.fn(), post: vi.fn(), patch: vi.fn() }
}))

const mockGet = vi.mocked(axiosInstance.get)

const MOCK_PACIENTES = [
  { id: 1, pacienteNombre: 'Juan Pérez',  tipoAtencion: 'CONSULTA', especialidad: 'Cardiología', prioridad: 1, estado: 'EN_ESPERA', fechaIngreso: '2024-01-10' },
  { id: 2, pacienteNombre: 'Ana López',   tipoAtencion: 'CIRUGIA',  especialidad: 'Cardiología', prioridad: 2, estado: 'EN_ESPERA', fechaIngreso: '2024-01-11' },
  { id: 3, pacienteNombre: 'Luis Vera',   tipoAtencion: 'CONSULTA', especialidad: 'Neurología',  prioridad: 1, estado: 'EN_ESPERA', fechaIngreso: '2024-01-12' },
]

beforeEach(() => {
  vi.clearAllMocks()
  window.open = vi.fn()
})

describe('Dashboard — estado de carga', () => {
  it('muestra "Cargando..." mientras espera la respuesta', () => {
    mockGet.mockReturnValue(new Promise(() => {}))
    render(<Dashboard />, { wrapper: MemoryRouter })
    expect(screen.getByText('Cargando...')).toBeInTheDocument()
  })
})

describe('Dashboard — carga exitosa', () => {
  beforeEach(() => {
    mockGet.mockResolvedValue({ data: { pacientes: MOCK_PACIENTES, totalEnEspera: 3 } })
  })

  it('muestra el título del dashboard', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Dashboard Médico — RedNorte')).toBeInTheDocument())
  })

  it('muestra el total en espera en la tarjeta de resumen', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('3')).toBeInTheDocument())
  })

  it('muestra la sección de especialidades activas', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Especialidades activas')).toBeInTheDocument())
  })

  it('muestra pacientes por especialidad en la tabla', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      // getAllByText porque Cardiología aparece en la tabla y en "Últimos ingresos"
      expect(screen.getAllByText('Cardiología').length).toBeGreaterThanOrEqual(1)
      expect(screen.getAllByText('Neurología').length).toBeGreaterThanOrEqual(1)
    })
  })

  it('muestra pacientes por tipo de atención', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('CONSULTA')).toBeInTheDocument()
      expect(screen.getByText('CIRUGIA')).toBeInTheDocument()
    })
  })

  it('muestra pacientes por prioridad', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('Alta')).toBeInTheDocument()
      expect(screen.getByText('Media')).toBeInTheDocument()
    })
  })

  it('muestra los últimos ingresos con nombre y especialidad', async () => {
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('Juan Pérez')).toBeInTheDocument()
      expect(screen.getByText('Ana López')).toBeInTheDocument()
    })
  })
})

describe('Dashboard — lista vacía', () => {
  it('muestra 0 en el contador total en espera', async () => {
    mockGet.mockResolvedValue({ data: { pacientes: [], totalEnEspera: 0 } })
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      const ceros = screen.getAllByText('0')
      expect(ceros.length).toBeGreaterThan(0)
    })
  })
})

describe('Dashboard — error al cargar', () => {
  it('oculta el loading cuando la petición falla', async () => {
    mockGet.mockRejectedValue(new Error('Network error'))
    render(<Dashboard />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.queryByText('Cargando...')).not.toBeInTheDocument()
    })
  })
})
