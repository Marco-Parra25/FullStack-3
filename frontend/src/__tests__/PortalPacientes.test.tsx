import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import axiosInstance from '../services/axiosInstance'
import PortalPacientes from '../pages/PortalPacientes'

vi.mock('../services/axiosInstance', () => ({
  default: { get: vi.fn(), post: vi.fn(), patch: vi.fn() }
}))

vi.mock('../components/Footer', () => ({
  default: () => <footer data-testid="footer" />
}))

const mockGet = vi.mocked(axiosInstance.get)

const MOCK_FICHA = {
  id: 1,
  pacienteNombre: 'Juan Pérez',
  pacienteRut: '12345678-9',
  tipoAtencion: 'CONSULTA',
  especialidad: 'Cardiología',
  prioridad: 2,
  estado: 'EN_ESPERA',
  fechaIngreso: '2024-01-10'
}

// Genera un token JWT falso con payload codificado en base64
const makeJwt = (payload: Record<string, unknown>) => {
  const encoded = btoa(JSON.stringify(payload))
  return `eyJhbGciOiJSUzI1NiJ9.${encoded}.fakesignature`
}

// Genera un token mock (plain base64, sin puntos)
const makeMockToken = (payload: Record<string, unknown>) =>
  btoa(JSON.stringify(payload))

beforeEach(() => {
  vi.clearAllMocks()
  sessionStorage.clear()
})

describe('PortalPacientes — sin token en sesión', () => {
  it('muestra error de sesión cuando no hay token', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => {
      expect(screen.getByText(/No se pudo obtener el RUT/)).toBeInTheDocument()
    })
  })

  it('no llama a la API sin token', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(mockGet).not.toHaveBeenCalled())
  })
})

describe('PortalPacientes — token JWT real (3 partes)', () => {
  it('extrae preferred_username y llama a /portal/rut/:rut', async () => {
    sessionStorage.setItem('token', makeJwt({ preferred_username: '12345678-9' }))
    mockGet.mockResolvedValue({
      data: { ficha: MOCK_FICHA, posicionEnEspera: 2, totalEnEspera: 10, pacientesEnEspecialidad: 3 }
    })

    render(<PortalPacientes />, { wrapper: MemoryRouter })

    await waitFor(() => {
      expect(mockGet).toHaveBeenCalledWith('/portal/rut/12345678-9')
    })
  })

  it('cuando preferred_username no está, usa sub como fallback', async () => {
    sessionStorage.setItem('token', makeJwt({ sub: '98765432-1' }))
    mockGet.mockResolvedValue({
      data: { ficha: null, posicionEnEspera: null, totalEnEspera: 0, pacientesEnEspecialidad: 0 }
    })

    render(<PortalPacientes />, { wrapper: MemoryRouter })

    await waitFor(() => {
      expect(mockGet).toHaveBeenCalledWith('/portal/rut/98765432-1')
    })
  })
})

describe('PortalPacientes — token mock (plain base64)', () => {
  it('extrae sub y llama a la API correctamente', async () => {
    sessionStorage.setItem('token', makeMockToken({ sub: '11111111-1' }))
    mockGet.mockResolvedValue({
      data: { ficha: null, posicionEnEspera: null, totalEnEspera: 0, pacientesEnEspecialidad: 0 }
    })

    render(<PortalPacientes />, { wrapper: MemoryRouter })

    await waitFor(() => {
      expect(mockGet).toHaveBeenCalledWith('/portal/rut/11111111-1')
    })
  })
})

describe('PortalPacientes — carga exitosa con ficha', () => {
  beforeEach(() => {
    sessionStorage.setItem('token', makeJwt({ preferred_username: '12345678-9' }))
    mockGet.mockResolvedValue({
      data: { ficha: MOCK_FICHA, posicionEnEspera: 2, totalEnEspera: 10, pacientesEnEspecialidad: 3 }
    })
  })

  it('muestra el nombre del paciente', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Juan Pérez')).toBeInTheDocument())
  })

  it('muestra la especialidad', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText('Cardiología')).toBeInTheDocument())
  })

  it('muestra la posición en lista de espera', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText(/Tu posición en lista de espera:/)).toBeInTheDocument())
  })

  it('muestra el total de pacientes en espera', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText(/Total pacientes en espera:/)).toBeInTheDocument())
  })

  it('muestra la cantidad de pacientes en la especialidad', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByText(/Pacientes en tu especialidad:/)).toBeInTheDocument())
  })

  it('renderiza el footer mockeado', async () => {
    render(<PortalPacientes />, { wrapper: MemoryRouter })
    await waitFor(() => expect(screen.getByTestId('footer')).toBeInTheDocument())
  })
})

describe('PortalPacientes — respuesta sin ficha', () => {
  it('muestra mensaje de sin citas registradas', async () => {
    sessionStorage.setItem('token', makeJwt({ preferred_username: '12345678-9' }))
    mockGet.mockResolvedValue({
      data: { ficha: null, posicionEnEspera: null, totalEnEspera: 5, pacientesEnEspecialidad: 0 }
    })

    render(<PortalPacientes />, { wrapper: MemoryRouter })

    await waitFor(() => {
      expect(screen.getByText(/No tienes citas registradas/)).toBeInTheDocument()
    })
  })
})

describe('PortalPacientes — error de API', () => {
  it('muestra mensaje de error cuando la petición falla', async () => {
    sessionStorage.setItem('token', makeJwt({ preferred_username: '12345678-9' }))
    mockGet.mockRejectedValue(new Error('Network error'))

    render(<PortalPacientes />, { wrapper: MemoryRouter })

    await waitFor(() => {
      expect(screen.getByText(/No se pudo cargar tu información/)).toBeInTheDocument()
    })
  })
})
