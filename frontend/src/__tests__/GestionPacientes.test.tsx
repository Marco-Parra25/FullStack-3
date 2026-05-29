import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import axiosInstance from '../services/axiosInstance'
import GestionPacientes from '../pages/GestionPacientes'

vi.mock('../services/axiosInstance', () => ({
  default: { get: vi.fn(), post: vi.fn(), patch: vi.fn() }
}))

const mockGet = vi.mocked(axiosInstance.get)
const mockPost = vi.mocked(axiosInstance.post)

const MOCK_PACIENTES = [
  { id: 1, rut: '11111111-1', nombre: 'Juan',  apellido: 'Pérez',  telefono: '+56912345678', email: 'juan@test.com' },
  { id: 2, rut: '22222222-2', nombre: 'Ana',   apellido: 'López',  telefono: '',             email: '' },
  { id: 3, rut: '33333333-3', nombre: 'Pedro', apellido: 'Vera',   telefono: '+56987654321', email: 'pedro@test.com' },
]

beforeEach(() => {
  vi.clearAllMocks()
  window.alert = vi.fn()
})

describe('GestionPacientes — estado de carga', () => {
  it('muestra "Cargando..." mientras espera la respuesta', () => {
    mockGet.mockReturnValue(new Promise(() => {}))
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    expect(screen.getByText('Cargando...')).toBeInTheDocument()
  })
})

describe('GestionPacientes — carga exitosa', () => {
  beforeEach(() => {
    mockGet.mockResolvedValue({ data: MOCK_PACIENTES })
  })

  it('muestra el título de la página', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Gestión de Pacientes — RedNorte')).toBeInTheDocument())
  })

  it('muestra el total de pacientes', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Total pacientes: 3')).toBeInTheDocument())
  })

  it('lista todos los pacientes cargados', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('Juan')).toBeInTheDocument()
      expect(screen.getByText('Ana')).toBeInTheDocument()
      expect(screen.getByText('Pedro')).toBeInTheDocument()
    })
  })

  it('muestra "-" cuando el teléfono está vacío', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => {
      const guiones = screen.getAllByText('-')
      expect(guiones.length).toBeGreaterThanOrEqual(1)
    })
  })
})

describe('GestionPacientes — filtro', () => {
  beforeEach(() => {
    mockGet.mockResolvedValue({ data: MOCK_PACIENTES })
  })

  it('filtra por nombre y oculta los que no coinciden', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por nombre, apellido o RUT'), {
      target: { value: 'juan' }
    })

    expect(screen.getByText('Juan')).toBeInTheDocument()
    expect(screen.queryByText('Ana')).not.toBeInTheDocument()
    expect(screen.queryByText('Pedro')).not.toBeInTheDocument()
  })

  it('filtra por RUT', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por nombre, apellido o RUT'), {
      target: { value: '22222222' }
    })

    expect(screen.getByText('Ana')).toBeInTheDocument()
    expect(screen.queryByText('Juan')).not.toBeInTheDocument()
  })

  it('muestra mensaje cuando no hay coincidencias', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por nombre, apellido o RUT'), {
      target: { value: 'zzzzzzzzz' }
    })

    expect(screen.getByText('No se encontraron pacientes con ese filtro')).toBeInTheDocument()
  })
})

describe('GestionPacientes — crear paciente', () => {
  beforeEach(() => {
    mockGet.mockResolvedValue({ data: MOCK_PACIENTES })
  })

  it('muestra alerta si faltan campos obligatorios', async () => {
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Crear paciente')).toBeInTheDocument())

    fireEvent.click(screen.getByText('Crear paciente'))

    expect(window.alert).toHaveBeenCalledWith('RUT, nombre y apellido son obligatorios')
  })

  it('crea paciente exitosamente y muestra alerta con nombre e ID', async () => {
    mockGet.mockResolvedValue({ data: MOCK_PACIENTES })
    mockPost.mockResolvedValue({ data: { id: 4, nombre: 'María' } })

    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Crear paciente')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('RUT (ej: 12345678-9)'), { target: { value: '44444444-4' } })
    fireEvent.change(screen.getByPlaceholderText('Nombre'), { target: { value: 'María' } })
    fireEvent.change(screen.getByPlaceholderText('Apellido'), { target: { value: 'García' } })
    fireEvent.click(screen.getByText('Crear paciente'))

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/admin/pacientes', expect.objectContaining({
        rut: '44444444-4', nombre: 'María', apellido: 'García'
      }))
      expect(window.alert).toHaveBeenCalledWith('Paciente María creado con ID 4')
    })
  })

  it('muestra alerta de datos inválidos si el servidor responde 400', async () => {
    mockPost.mockRejectedValue({ response: { status: 400 } })

    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Crear paciente')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('RUT (ej: 12345678-9)'), { target: { value: 'invalido' } })
    fireEvent.change(screen.getByPlaceholderText('Nombre'), { target: { value: 'Test' } })
    fireEvent.change(screen.getByPlaceholderText('Apellido'), { target: { value: 'Test' } })
    fireEvent.click(screen.getByText('Crear paciente'))

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Datos inválidos. Verifica el formato del RUT y email')
    })
  })

  it('muestra alerta genérica si el servidor falla por otro motivo', async () => {
    mockPost.mockRejectedValue({ response: { status: 500 } })

    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Crear paciente')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('RUT (ej: 12345678-9)'), { target: { value: '44444444-4' } })
    fireEvent.change(screen.getByPlaceholderText('Nombre'), { target: { value: 'Test' } })
    fireEvent.change(screen.getByPlaceholderText('Apellido'), { target: { value: 'Test' } })
    fireEvent.click(screen.getByText('Crear paciente'))

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Error al crear paciente')
    })
  })
})

describe('GestionPacientes — error al cargar', () => {
  it('oculta el loading cuando la petición falla', async () => {
    mockGet.mockRejectedValue(new Error('Network error'))
    render(<GestionPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.queryByText('Cargando...')).not.toBeInTheDocument()
    })
  })
})
