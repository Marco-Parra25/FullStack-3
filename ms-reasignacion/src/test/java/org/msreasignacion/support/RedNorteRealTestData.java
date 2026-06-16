package org.msreasignacion.support;

import org.msreasignacion.domain.event.CupoLiberadoEvent;
import org.msreasignacion.domain.model.Paciente;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Fixtures basados en rednorte-project/ms-lista-espera/src/main/resources/db/migration/V2__datos_iniciales.sql.
 */
public final class RedNorteRealTestData {

    public static final String CARDIOLOGIA = "Cardiolog\u00eda";
    public static final String TRAUMATOLOGIA = "Traumatolog\u00eda";

    public static final String WAITLIST_CARDIOLOGIA_ID = "1";
    public static final String WAITLIST_TRAUMATOLOGIA_ID = "2";
    public static final LocalDate WAITLIST_CARDIOLOGIA_FECHA_INGRESO = LocalDate.of(2025, 6, 15);

    public static final Long PACIENTE_MARIA_ID = 1L;
    public static final String PACIENTE_MARIA_RUT = "12345678-9";
    public static final String PACIENTE_MARIA_NOMBRE = "Mar\u00eda";
    public static final String PACIENTE_MARIA_APELLIDO = "Gonz\u00e1lez";
    public static final String PACIENTE_MARIA_TELEFONO = "+56912345678";
    public static final String PACIENTE_MARIA_EMAIL = "maria.gonzalez@email.cl";

    public static final UUID REASIGNACION_CARDIOLOGIA_ID =
            UUID.fromString("11111111-1111-4111-8111-111111111111");
    public static final LocalDateTime REASIGNACION_CARDIOLOGIA_FECHA =
            LocalDateTime.of(2025, 6, 16, 9, 30);

    private RedNorteRealTestData() {
    }

    public static Paciente pacienteMariaGonzalez() {
        return new Paciente(
                PACIENTE_MARIA_ID,
                PACIENTE_MARIA_RUT,
                PACIENTE_MARIA_NOMBRE,
                PACIENTE_MARIA_APELLIDO,
                PACIENTE_MARIA_TELEFONO,
                PACIENTE_MARIA_EMAIL
        );
    }

    public static CupoLiberadoEvent cupoLiberadoCardiologia() {
        return new CupoLiberadoEvent(WAITLIST_CARDIOLOGIA_ID, CARDIOLOGIA);
    }

    public static CupoLiberadoEvent cupoLiberadoTraumatologia() {
        return new CupoLiberadoEvent(WAITLIST_TRAUMATOLOGIA_ID, TRAUMATOLOGIA);
    }

    public static UUID cupoOrigenUuid(String waitlistItemId) {
        return UUID.nameUUIDFromBytes(("cupo-liberado:" + waitlistItemId).getBytes(StandardCharsets.UTF_8));
    }
}
