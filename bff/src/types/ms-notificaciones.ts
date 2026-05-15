//Enums

export type TipoNotificacion = 'SMS' | 'EMAIL';

export type EstadoNotificacion = 'ENVIADA' | 'PENDIENTE' | 'FALLIDA';

export type EstadoCampania = 'PENDIENTE' | 'EN_PROCESO' | 'COMPLETADA' | 'FALLIDA';

//Responses 

/** POST /api/v1/notificaciones/enviar */
export interface NotificacionResponse {
  notificacionId: string;
  /** RUT del paciente */
  pacienteId: string;
  tipo: TipoNotificacion;
  mensaje: string;
  /** Teléfono (+56...) o email según tipo */
  destino: string;
  estado: EstadoNotificacion;
  /** ISO datetime string */
  fechaEnvio: string;
}

/** Item del historial — GET /api/v1/notificaciones/historial */
export interface NotificacionHistorialItem {
  id: string;
  /** RUT del paciente */
  pacienteId: string;
  pacienteNombre: string;
  tipo: TipoNotificacion;
  mensaje: string;
  destino: string;
  estado: EstadoNotificacion;
  /** ISO datetime string */
  fechaEnvio: string;
}

export interface NotificacionHistorialResponse {
  content: NotificacionHistorialItem[];
}

/** POST /api/v1/notificaciones/campania */
export interface CampaniaResponse {
  campaniaId: string;
  nombre: string;
  mensaje: string;
  tipo: TipoNotificacion;
  pacientes: number;
  enviados: number;
  fallidos: number;
  estado: EstadoCampania;
  /** ISO datetime string */
  fechaInicio: string;
  /** ISO datetime string */
  fechaFin: string;
}

//Requests 

/** POST /api/v1/notificaciones/enviar */
export interface EnviarNotificacionRequest {
  pacienteId: string;
  tipo: TipoNotificacion;
  mensaje: string;
  destino: string;
}

/** POST /api/v1/notificaciones/campania */
export interface CrearCampaniaRequest {
  nombre: string;
  mensaje: string;
  tipo: TipoNotificacion;
  pacientesIds: string[];
}
