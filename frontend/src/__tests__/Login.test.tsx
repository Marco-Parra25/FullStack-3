import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import Login from '../pages/Login'

const mockNavigate = vi.fn()

vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual<typeof import('react-router-dom')>('react-router-dom')
  return { ...actual, useNavigate: () => mockNavigate }
})

const renderLogin = (setRol = vi.fn()) =>
  render(<Login setRol={setRol} />, { wrapper: MemoryRouter })

const fillAndSubmit = (usuario: string, password: string) => {
  fireEvent.change(screen.getByPlaceholderText('Ingresa tu usuario'), { target: { value: usuario } })
  fireEvent.change(screen.getByPlaceholderText('Ingresa tu contraseña'), { target: { value: password } })
  fireEvent.click(screen.getByText('Iniciar sesión'))
}

// ─── Modo mock ────────────────────────────────────────────────────────────────

describe('Login — modo mock (VITE_USE_MOCK_AUTH=true)', () => {
  beforeEach(() => {
    ;(import.meta.env as Record<string, unknown>).VITE_USE_MOCK_AUTH = 'true'
    mockNavigate.mockClear()
    sessionStorage.clear()
  })
  afterEach(() => {
    delete (import.meta.env as Record<string, unknown>).VITE_USE_MOCK_AUTH
  })

  it('renderiza el formulario con usuario, contraseña y botón', () => {
    renderLogin()
    expect(screen.getByPlaceholderText('Ingresa tu usuario')).toBeInTheDocument()
    expect(screen.getByPlaceholderText('Ingresa tu contraseña')).toBeInTheDocument()
    expect(screen.getByText('Iniciar sesión')).toBeInTheDocument()
  })

  it('muestra los usuarios de prueba disponibles', () => {
    renderLogin()
    expect(screen.getByText(/admin \/ 1234/)).toBeInTheDocument()
    expect(screen.getByText(/medico \/ 1234/)).toBeInTheDocument()
    expect(screen.getByText(/paciente \/ 1234/)).toBeInTheDocument()
  })

  it('admin exitoso: llama setRol("admin") y navega a /admin', () => {
    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('admin', '1234')
    expect(setRol).toHaveBeenCalledWith('admin')
    expect(mockNavigate).toHaveBeenCalledWith('/admin')
  })

  it('medico exitoso: llama setRol("medico") y navega a /dashboard', () => {
    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('medico', '1234')
    expect(setRol).toHaveBeenCalledWith('medico')
    expect(mockNavigate).toHaveBeenCalledWith('/dashboard')
  })

  it('paciente exitoso: llama setRol("paciente") y navega a /portal', () => {
    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('paciente', '1234')
    expect(setRol).toHaveBeenCalledWith('paciente')
    expect(mockNavigate).toHaveBeenCalledWith('/portal')
  })

  it('credenciales incorrectas muestran mensaje de error', () => {
    renderLogin()
    fillAndSubmit('admin', 'wrong')
    expect(screen.getByText('Usuario o contraseña incorrectos')).toBeInTheDocument()
  })

  it('Enter en el campo contraseña dispara el login', () => {
    const setRol = vi.fn()
    renderLogin(setRol)
    fireEvent.change(screen.getByPlaceholderText('Ingresa tu usuario'), { target: { value: 'admin' } })
    const passInput = screen.getByPlaceholderText('Ingresa tu contraseña')
    fireEvent.change(passInput, { target: { value: '1234' } })
    fireEvent.keyDown(passInput, { key: 'Enter' })
    expect(setRol).toHaveBeenCalledWith('admin')
  })

  it('guarda el token en sessionStorage tras login exitoso', () => {
    renderLogin()
    fillAndSubmit('admin', '1234')
    expect(sessionStorage.getItem('token')).not.toBeNull()
  })
})

// ─── Modo Keycloak ────────────────────────────────────────────────────────────

describe('Login — modo Keycloak (VITE_USE_MOCK_AUTH=false)', () => {
  beforeEach(() => {
    ;(import.meta.env as Record<string, unknown>).VITE_USE_MOCK_AUTH = 'false'
    ;(import.meta.env as Record<string, unknown>).VITE_KEYCLOAK_URL = 'http://localhost:3001'
    mockNavigate.mockClear()
    sessionStorage.clear()
  })
  afterEach(() => {
    delete (import.meta.env as Record<string, unknown>).VITE_USE_MOCK_AUTH
    delete (import.meta.env as Record<string, unknown>).VITE_KEYCLOAK_URL
    vi.restoreAllMocks()
  })

  const makeToken = (roles: string[]) => {
    const payload = { preferred_username: 'testuser', realm_access: { roles } }
    return `h.${btoa(JSON.stringify(payload))}.s`
  }

  it('no muestra usuarios de prueba en modo Keycloak', () => {
    renderLogin()
    expect(screen.queryByText(/admin \/ 1234/)).not.toBeInTheDocument()
  })

  it('login exitoso como ADMIN navega a /admin', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ token: makeToken(['ADMIN']) })
    } as Response)

    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('admin', 'admin123')

    await waitFor(() => {
      expect(setRol).toHaveBeenCalledWith('admin')
      expect(mockNavigate).toHaveBeenCalledWith('/admin')
    })
  })

  it('login exitoso como MEDICO navega a /dashboard', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ token: makeToken(['MEDICO']) })
    } as Response)

    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('medico', 'medico123')

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard')
    })
  })

  it('login exitoso sin rol admin/medico navega a /portal', async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ token: makeToken(['PACIENTE']) })
    } as Response)

    const setRol = vi.fn()
    renderLogin(setRol)
    fillAndSubmit('paciente', 'paciente123')

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/portal')
    })
  })

  it('respuesta !ok muestra error de credenciales', async () => {
    global.fetch = vi.fn().mockResolvedValue({ ok: false } as Response)

    renderLogin()
    fillAndSubmit('admin', 'wrong')

    await waitFor(() => {
      expect(screen.getByText('Usuario o contraseña incorrectos')).toBeInTheDocument()
    })
  })

  it('error de red muestra mensaje de conexión fallida', async () => {
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'))

    renderLogin()
    fireEvent.click(screen.getByText('Iniciar sesión'))

    await waitFor(() => {
      expect(screen.getByText('No se pudo conectar con el servidor de autenticación')).toBeInTheDocument()
    })
  })

  it('muestra "Iniciando sesión..." mientras carga', async () => {
    global.fetch = vi.fn().mockImplementation(() => new Promise(() => {})) // never resolves

    renderLogin()
    fireEvent.click(screen.getByText('Iniciar sesión'))

    expect(await screen.findByText('Iniciando sesión...')).toBeInTheDocument()
  })
})
