import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import axiosInstance from '../services/axiosInstance'
import PanelAdmin from '../pages/PanelAdmin'

vi.mock('../services/axiosInstance', () => ({
  default: { get: vi.fn(), post: vi.fn(), patch: vi.fn() }
}))

const mockGet   = vi.mocked(axiosInstance.get)
const mockPost  = vi.mocked(axiosInstance.post)
const mockPatch = vi.mocked(axiosInstance.patch)

const MOCK_LISTA = [
  { id: 1, pacienteNombre: 'Juan Pérez',  pacienteRut: '11111111-1', tipoAtencion: 'CONSULTA', especialidad: 'Cardiología', prioridad: 1, estado: 'EN_ESPERA', fechaIngreso: '2024-01-10' },
  { id: 2, pacienteNombre: 'Ana López',   pacienteRut: '22222222-2', tipoAtencion: 'CIRUGIA',  especialidad: 'Neurología',  prioridad: 2, estado: 'EN_ESPERA', fechaIngreso: '2024-01-11' },
]

const MOCK_PACIENTES_DISPONIBLES = [
  { id: 1, nombre: 'Juan',  apellido: 'Pérez', rut: '11111111-1' },
  { id: 2, nombre: 'Ana',   apellido: 'López', rut: '22222222-2' },
]

const setupMocks = () => {
  mockGet.mockImplementation((url: string) => {
    if (url === '/admin/lista')     return Promise.resolve({ data: { pacientes: MOCK_LISTA, totalEnEspera: 2 } })
    if (url === '/admin/pacientes') return Promise.resolve({ data: MOCK_PACIENTES_DISPONIBLES })
    return Promise.reject(new Error(`Unexpected URL: ${url}`))
  })
}

beforeEach(() => {
  vi.clearAllMocks()
  window.alert   = vi.fn()
  window.confirm = vi.fn(() => true)
})

describe('PanelAdmin — estado de carga', () => {
  it('muestra "Cargando..." mientras espera la respuesta', () => {
    mockGet.mockReturnValue(new Promise(() => {}))
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    expect(screen.getByText('Cargando...')).toBeInTheDocument()
  })
})

describe('PanelAdmin — carga exitosa', () => {
  beforeEach(setupMocks)

  it('muestra el título Panel Administrativo', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Panel Administrativo — RedNorte')).toBeInTheDocument())
  })

  it('muestra el total en espera', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Total en espera: 2')).toBeInTheDocument())
  })

  it('muestra los pacientes en la tabla de lista de espera', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('Juan Pérez')).toBeInTheDocument()
      expect(screen.getByText('Ana López')).toBeInTheDocument()
    })
  })

  it('muestra los pacientes disponibles en el selector', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText('1 - Juan Pérez')).toBeInTheDocument()
      expect(screen.getByText('2 - Ana López')).toBeInTheDocument()
    })
  })
})

describe('PanelAdmin — filtros', () => {
  beforeEach(setupMocks)

  it('filtra por nombre y oculta los que no coinciden', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan Pérez')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por nombre'), { target: { value: 'juan' } })

    expect(screen.getByText('Juan Pérez')).toBeInTheDocument()
    expect(screen.queryByText('Ana López')).not.toBeInTheDocument()
  })

  it('filtra por especialidad', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Ana López')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por especialidad'), { target: { value: 'neurología' } })

    expect(screen.getByText('Ana López')).toBeInTheDocument()
    expect(screen.queryByText('Juan Pérez')).not.toBeInTheDocument()
  })

  it('filtra por RUT', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan Pérez')).toBeInTheDocument())

    fireEvent.change(screen.getByPlaceholderText('Filtrar por RUT'), { target: { value: '22222222' } })

    expect(screen.getByText('Ana López')).toBeInTheDocument()
    expect(screen.queryByText('Juan Pérez')).not.toBeInTheDocument()
  })
})

describe('PanelAdmin — registrar paciente', () => {
  beforeEach(setupMocks)

  it('muestra alerta si el formulario está incompleto', async () => {
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Registrar')).toBeInTheDocument())

    fireEvent.click(screen.getByText('Registrar'))

    expect(window.alert).toHaveBeenCalledWith('Completa todos los campos')
  })

  it('registra exitosamente y muestra alerta de confirmación', async () => {
    mockPost.mockResolvedValue({ data: {} })

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('1 - Juan Pérez')).toBeInTheDocument())

    fireEvent.change(screen.getByTitle('Seleccionar paciente'), { target: { value: '1' } })
    fireEvent.change(screen.getByPlaceholderText('Especialidad'), { target: { value: 'Cardiología' } })
    fireEvent.click(screen.getByText('Registrar'))

    await waitFor(() => {
      expect(mockPost).toHaveBeenCalledWith('/admin/registrar', expect.objectContaining({
        pacienteId: 1, especialidad: 'Cardiología'
      }))
      expect(window.alert).toHaveBeenCalledWith('Paciente registrado exitosamente')
    })
  })

  it('muestra alerta de paciente no encontrado si el servidor responde 404', async () => {
    mockPost.mockRejectedValue({ response: { status: 404 } })

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('1 - Juan Pérez')).toBeInTheDocument())

    fireEvent.change(screen.getByTitle('Seleccionar paciente'), { target: { value: '1' } })
    fireEvent.change(screen.getByPlaceholderText('Especialidad'), { target: { value: 'Test' } })
    fireEvent.click(screen.getByText('Registrar'))

    await waitFor(() => {
      expect(window.alert).toHaveBeenCalledWith('Paciente no encontrado en el sistema')
    })
  })
})

describe('PanelAdmin — acciones sobre la lista', () => {
  beforeEach(setupMocks)

  it('cancela la cita tras confirmar el diálogo', async () => {
    mockPatch.mockResolvedValue({ data: {} })

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getAllByText('Cancelar')[0]).toBeInTheDocument())

    fireEvent.click(screen.getAllByText('Cancelar')[0])

    await waitFor(() => {
      expect(mockPatch).toHaveBeenCalledWith('/admin/cancelar/1')
      expect(window.alert).toHaveBeenCalledWith(
        'Cita cancelada exitosamente. El sistema reasignará y notificará al paciente automáticamente.'
      )
    })
  })

  it('no cancela si el usuario rechaza el diálogo', async () => {
    window.confirm = vi.fn(() => false)

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getAllByText('Cancelar')[0]).toBeInTheDocument())

    fireEvent.click(screen.getAllByText('Cancelar')[0])

    expect(mockPatch).not.toHaveBeenCalled()
  })

  it('cambia estado a ASIGNADO al hacer clic en "En atención"', async () => {
    mockPatch.mockResolvedValue({ data: {} })

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getAllByText('En atención')[0]).toBeInTheDocument())

    fireEvent.click(screen.getAllByText('En atención')[0])

    await waitFor(() => {
      expect(mockPatch).toHaveBeenCalledWith('/admin/estado/1', { estado: 'ASIGNADO' })
      expect(window.alert).toHaveBeenCalledWith('Estado actualizado')
    })
  })

  it('cambia estado a ATENDIDO al hacer clic en "Atendido"', async () => {
    mockPatch.mockResolvedValue({ data: {} })

    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getAllByText('Atendido')[0]).toBeInTheDocument())

    fireEvent.click(screen.getAllByText('Atendido')[0])

    await waitFor(() => {
      expect(mockPatch).toHaveBeenCalledWith('/admin/estado/1', { estado: 'ATENDIDO' })
    })
  })
})

describe('PanelAdmin — error al cargar', () => {
  it('oculta el loading cuando la petición falla', async () => {
    mockGet.mockRejectedValue(new Error('Network error'))
    render(<PanelAdmin />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.queryByText('Cargando...')).not.toBeInTheDocument()
    })
  })
})
