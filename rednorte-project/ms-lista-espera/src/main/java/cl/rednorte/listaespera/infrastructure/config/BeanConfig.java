package cl.rednorte.listaespera.infrastructure.config;

import cl.rednorte.listaespera.domain.port.input.WaitlistUseCase;
import cl.rednorte.listaespera.domain.port.output.PacienteRepository;
import cl.rednorte.listaespera.domain.port.output.WaitlistRepository;
import cl.rednorte.listaespera.domain.service.WaitlistService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public WaitlistUseCase waitlistUseCase(WaitlistRepository waitlistRepository,
                                            PacienteRepository pacienteRepository) {
        return new WaitlistService(waitlistRepository, pacienteRepository);
    }
}
