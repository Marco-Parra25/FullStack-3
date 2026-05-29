import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import Navbar from '../components/Navbar'

const mockNavigate = vi.fn()

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
  return { ...actual, useNavigate: () => mockNavigate }
})

beforeEach(() => {
  mockNavigate.mockClear()
  sessionStorage.setItem('token', 'test-token')
})
afterEach(() => sessionStorage.clear())

const renderNavbar = (rol: string, setRol = vi.fn()) =>
  render(<Navbar rol={rol} setRol={setRol} />, { wrapper: MemoryRouter })

describe('Navbar — rol admin', () => {
  it('muestra Panel Admin y Gestión Pacientes', () => {
    renderNavbar('admin')
    expect(screen.getByText('Panel Admin')).toBeInTheDocument()
    expect(screen.getByText('Gestión Pacientes')).toBeInTheDocument()
  })

  it('muestra la etiqueta Administrativo', () => {
    renderNavbar('admin')
    expect(screen.getByText('Administrativo')).toBeInTheDocument()
  })

  it('no muestra links de otros roles', () => {
    renderNavbar('admin')
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
    expect(screen.queryByText('Portal')).not.toBeInTheDocument()
  })
})

describe('Navbar — rol medico', () => {
  it('muestra solo el link Dashboard', () => {
    renderNavbar('medico')
    expect(screen.getByText('Dashboard')).toBeInTheDocument()
    expect(screen.queryByText('Panel Admin')).not.toBeInTheDocument()
    expect(screen.queryByText('Portal')).not.toBeInTheDocument()
  })

  it('muestra la etiqueta Médico', () => {
    renderNavbar('medico')
    expect(screen.getByText('Médico')).toBeInTheDocument()
  })
})

describe('Navbar — rol paciente', () => {
  it('muestra solo el link Portal', () => {
    renderNavbar('paciente')
    expect(screen.getByText('Portal')).toBeInTheDocument()
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument()
    expect(screen.queryByText('Panel Admin')).not.toBeInTheDocument()
  })

  it('muestra la etiqueta Paciente', () => {
    renderNavbar('paciente')
    expect(screen.getByText('Paciente')).toBeInTheDocument()
  })
})

describe('Navbar — cerrar sesión', () => {
  it('elimina el token de sessionStorage', () => {
    renderNavbar('admin')
    fireEvent.click(screen.getByText('Cerrar sesión'))
    expect(sessionStorage.getItem('token')).toBeNull()
  })

  it('llama setRol con string vacío', () => {
    const setRol = vi.fn()
    renderNavbar('admin', setRol)
    fireEvent.click(screen.getByText('Cerrar sesión'))
    expect(setRol).toHaveBeenCalledWith('')
  })

  it('navega a la raíz', () => {
    renderNavbar('admin')
    fireEvent.click(screen.getByText('Cerrar sesión'))
    expect(mockNavigate).toHaveBeenCalledWith('/')
  })
})
