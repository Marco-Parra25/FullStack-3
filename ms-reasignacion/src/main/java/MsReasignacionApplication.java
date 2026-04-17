package org;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "org.domain.model") // Le decimos a Spring dónde están las tablas
@EnableJpaRepositories(basePackages = "org.infrastructure.repository") // Le decimos dónde está el repositorio
public class MsReasignacionApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsReasignacionApplication.class, args);
    }
}