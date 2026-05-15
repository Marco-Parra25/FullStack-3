//Enums 

export type EstadoReasignacion = 'ASIGNADO' | 'PENDIENTE' | 'COMPLETADA' | 'FALLIDA';

//Responses 

/** Mapea Reasignacion.java entity */
export interface ReasignacionResponse {
  /** UUID */
  id: string;
  pacienteRut: string;
  especialidad: string;
  /** ISO datetime string (Java LocalDateTime) */
  fechaAsignacion: string;
  estado: EstadoReasignacion;
  /** UUID del cupo de origen que fue liberado */
  cupoOrigenId: string;
}

//Eventos Kafka-documentación

/** Evento que dispara la reasignación*/
export interface CupoLiberadoEvent {
  cupoId: string;
  especialidad: string;
}

/** Evento que publica el resultado*/
export interface CupoAsignadoEvent {
  pacienteRut: string;
  telefono: string;
  email: string;
  especialidad: string;
}
