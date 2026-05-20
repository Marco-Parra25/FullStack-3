package cl.rednorte.listaespera.domain.port.output;

import cl.rednorte.listaespera.domain.event.CupoLiberadoEvent;

public interface CupoLiberadoPublisherPort {
    void publicar(CupoLiberadoEvent event);
}
