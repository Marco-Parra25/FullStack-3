package org.msreasignacion;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.kafka.enabled=false"
})
@ActiveProfiles("test")
class MsReasignacionApplicationTest {

    @Test
    void contextLoads() {
        // Al definir las properties aquí arriba, obligamos a Spring
        // a usar H2 y apagar Flyway, sin importar lo que diga el application.yml
    }
}