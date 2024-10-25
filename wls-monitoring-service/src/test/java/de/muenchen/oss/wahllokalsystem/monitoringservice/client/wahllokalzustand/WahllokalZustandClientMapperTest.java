package de.muenchen.oss.wahllokalsystem.monitoringservice.client.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.DruckzustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.eai.aou.model.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.DruckzustandModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandModel;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand.WahllokalZustandOperation;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { WahllokalZustandClientMapperImpl.class, DruckzustandClientMapperImpl.class })
class WahllokalZustandClientMapperTest {

    @Autowired
    private WahllokalZustandClientMapper unitUnderTest;// = Mappers.getMapper(WahllokalZustandClientMapper.class);

    @Nested
    class ToDTO {

        @Test
        void isMappedWithSomething() {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val zeitpunkt = LocalDateTime.now();

            for (WahllokalZustandOperation operation : WahllokalZustandOperation.values()) {
                val wahllokalZustandModel = createWahllokalZustandModel(operation, wahlID, wahlbezirkID, zeitpunkt.atOffset(ZoneOffset.UTC));
                Assertions.assertThat(wahllokalZustandModel.wahlbezirkID()).isNotNull();
                switch (operation) {
                case POST_LASTSEEN -> Assertions.assertThat(wahllokalZustandModel.zuletztGesehen()).isNotNull();
                case POST_LETZTEABMELDUNG -> Assertions.assertThat(wahllokalZustandModel.letzteAbmeldung()).isNotNull();
                case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT -> {
                    Assertions.assertThat(wahllokalZustandModel.druckzustaende()).isNotNull();
                    wahllokalZustandModel.druckzustaende().forEach((druckzustand) -> {
                        Assertions.assertThat(druckzustand.wahlID()).isNotNull();
                        Assertions.assertThat(druckzustand.schnellmeldungSendenUhrzeit()).isNotNull();
                    });
                }
                case POST_SCHNELLMELDUNG_DRUCKUHRZEIT -> {
                    Assertions.assertThat(wahllokalZustandModel.druckzustaende()).isNotNull();
                    wahllokalZustandModel.druckzustaende().forEach((druckzustand) -> {
                        Assertions.assertThat(druckzustand.wahlID()).isNotNull();
                        Assertions.assertThat(druckzustand.schnellmeldungDruckUhrzeit()).isNotNull();
                    });
                }
                case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT -> {
                    Assertions.assertThat(wahllokalZustandModel.druckzustaende()).isNotNull();
                    wahllokalZustandModel.druckzustaende().forEach((druckzustand) -> {
                        Assertions.assertThat(druckzustand.wahlID()).isNotNull();
                        Assertions.assertThat(druckzustand.niederschriftSendenUhrzeit()).isNotNull();
                    });
                }
                case POST_NIEDERSCHRIFT_DRUCKUHRZEIT -> {
                    Assertions.assertThat(wahllokalZustandModel.druckzustaende()).isNotNull();
                    wahllokalZustandModel.druckzustaende().forEach((druckzustand) -> {
                        Assertions.assertThat(druckzustand.wahlID()).isNotNull();
                        Assertions.assertThat(druckzustand.niederschriftDruckUhrzeit()).isNotNull();
                    });
                }
                }
                val result = unitUnderTest.toDTO(wahllokalZustandModel);
                val expectedWahllokalZustandDTO = createWahllokalZustandDTO(operation, wahlID, wahlbezirkID, zeitpunkt.atOffset(ZoneOffset.UTC));
                Assertions.assertThat(result).isEqualTo(expectedWahllokalZustandDTO);
            }
        }

        private WahllokalZustandDTO createWahllokalZustandDTO(WahllokalZustandOperation wahllokalZustandOperation, final String wahlID,
                final String wahlbezirkID, OffsetDateTime zeitpunkt) {
            return switch (wahllokalZustandOperation) {
            case POST_LASTSEEN -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).zuletztGesehen(zeitpunkt);
            case POST_LETZTEABMELDUNG -> new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID).letzteAbmeldung(zeitpunkt);
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .schnellmeldungSendenUhrzeit(zeitpunkt)));
            case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .schnellmeldungDruckUhrzeit(zeitpunkt)));
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .niederschriftSendenUhrzeit(zeitpunkt)));
            case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                new WahllokalZustandDTO().wahlbezirkID(wahlbezirkID)
                        .druckzustaende(
                                Set.of(
                                        new DruckzustandDTO()
                                                .wahlID(wahlID)
                                                .niederschriftDruckUhrzeit(zeitpunkt)));

            };
        }

        private WahllokalZustandModel createWahllokalZustandModel(WahllokalZustandOperation wahllokalZustandOperation, final String wahlID,
                final String wahlbezirkID, OffsetDateTime zeitpunkt) {
            WahllokalZustandModel.builder().build();
            return switch (wahllokalZustandOperation) {
            case POST_LASTSEEN ->
                WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID).zuletztGesehen(zeitpunkt.toLocalDateTime()).druckzustaende(Set.of()).build();
            case POST_LETZTEABMELDUNG ->
                WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID).letzteAbmeldung(zeitpunkt.toLocalDateTime()).druckzustaende(Set.of()).build();
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .schnellmeldungSendenUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_SCHNELLMELDUNG_DRUCKUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .schnellmeldungDruckUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .niederschriftSendenUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            case POST_NIEDERSCHRIFT_DRUCKUHRZEIT -> WahllokalZustandModel.builder().wahlbezirkID(wahlbezirkID)
                    .druckzustaende(
                            Set.of(
                                    DruckzustandModel.builder()
                                            .wahlID(wahlID)
                                            .niederschriftDruckUhrzeit(zeitpunkt.toLocalDateTime())
                                            .build()))
                    .build();
            };
        }

    }
}
