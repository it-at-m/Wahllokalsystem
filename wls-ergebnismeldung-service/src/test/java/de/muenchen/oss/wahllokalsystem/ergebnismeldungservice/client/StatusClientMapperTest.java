package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.DruckdatenDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.SendungsdatenDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StatusClientMapperTest {

    StatusClientMapper unitUnderTest = Mappers.getMapper(StatusClientMapper.class);

    @Nested
    class ToDruckdatenDTO {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toDruckdatenDTO(null, null)).isNull();
        }

        @Test
        void should_returnDruckdaten_when_dataIsGiven() {
            val dateTimeOfEvent = LocalDateTime.now();
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            val result = unitUnderTest.toDruckdatenDTO(bezirkUndWahlID, dateTimeOfEvent);

            val expectedResult = new DruckdatenDTO().bezirkUndWahlID(
                    new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.BezirkUndWahlID().wahlbezirkID(
                            bezirkUndWahlID.getWahlbezirkID())
                            .wahlID(bezirkUndWahlID.getWahlID()))
                    .druckuhrzeit(dateTimeOfEvent);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToSendungsdatenDTO {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toSendungsdatenDTO(null, null)).isNull();
        }

        @Test
        void should_returnSendungsdaten_when_dataIsGiven() {
            val dateTimeOfEvent = LocalDateTime.now();
            val bezirkUndWahlID = new BezirkUndWahlID("wahlID", "wahlbezirkID");

            val result = unitUnderTest.toSendungsdatenDTO(bezirkUndWahlID, dateTimeOfEvent);

            val expectedResult = new SendungsdatenDTO().bezirkUndWahlID(
                    new de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.monitoring.model.BezirkUndWahlID().wahlbezirkID(
                            bezirkUndWahlID.getWahlbezirkID())
                            .wahlID(bezirkUndWahlID.getWahlID()))
                    .sendungsuhrzeit(dateTimeOfEvent);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
