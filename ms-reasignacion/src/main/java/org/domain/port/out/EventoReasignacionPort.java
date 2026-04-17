package org.domain.port.out;

public interface EventoReasignacionPort {
    void publicarCupoAsignado(String pacienteRut, String telefono, String email, String especialidad);
}