//Enums 

/** Mapea EstadoEspera.java */
export type EstadoEspera = 'EN_ESPERA' | 'ASIGNADO' | 'CANCELADO' | 'ATENDIDO';

/** Mapea TipoAtencion.java */
export type TipoAtencion = 'CONSULTA' | 'CIRUGIA' | 'URGENCIA_DIFERIDA';

//Responses 

/** Mapea WaitlistResponse.java record — GET /api/v1/waitlist, GET /api/v1/waitlist/{id} */
export interface WaitlistResponse {
  id: number;
  pacienteNombre: string;
  pacienteRut: string;
  tipoAtencion: TipoAtencion;
  especialidad: string;
  prioridad: number;
  estado: EstadoEspera;
  /** Fecha en formato ISO "yyyy-MM-dd" (Java LocalDate) */
  fechaIngreso: string;
}

/** Mapea PacienteResponse.java record — GET /api/v1/pacientes, GET /api/v1/pacientes/{id} */
export interface PacienteResponse {
  id: number;
  rut: string;
  nombre: string;
  apellido: string;
  telefono: string | null;
  email: string | null;
}

/** GET /api/v1/waitlist/count */
export interface WaitlistCountResponse {
  enEspera: number;
}

//Requests 

/** Mapea RegistroRequest.java record — POST /api/v1/waitlist */
export interface RegistroWaitlistRequest {
  pacienteId: number;
  tipoAtencion: TipoAtencion;
  especialidad: string;
}

/** Mapea PacienteRequest.java record — POST /api/v1/pacientes */
export interface PacienteRequest {
  /** Formato válido: "12345678-9" */
  rut: string;
  nombre: string;
  apellido: string;
  /** Formato válido: "+56912345678" */
  telefono?: string;
  email?: string;
}
