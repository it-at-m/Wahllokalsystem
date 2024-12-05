package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.MeldungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.StatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.ValidierungsstatusModel;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class StatusDTOMapperTest {

    StatusDTOMapper unitUnderTest = Mappers.getMapper(StatusDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_return_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnDTO_when_givenModel() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val now = LocalDateTime.now();
            val afterNow = now.plusDays(1);
            val modelToMap = new StatusModel(new BezirkUndWahlID(wahlID, wahlbezirkID), new MeldungModel(ValidierungsstatusModel.VALIDE, true, true, now),
                    new MeldungModel(ValidierungsstatusModel.NICHT_VALIDIERT, false, false, afterNow));

            val result = unitUnderTest.toDTO(modelToMap);

            val expectedResult = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, now),
                    new MeldungDTO(ValidierungsstatusDTO.NICHT_VALIDIERT, false, false, afterNow));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(ValidierungsstatusModel.class)
        void should_mapToEnumWithSameName_when_givenModelValidierungsstatusEnumValue(final ValidierungsstatusModel validierungsstatus) {
            val modelTopMap = new StatusModel(null, new MeldungModel(validierungsstatus, false, null, null),
                    new MeldungModel(validierungsstatus, false, null, null));

            val result = unitUnderTest.toDTO(modelTopMap);

            Assertions.assertThat(result.niederschrift().validierungsstatus().name()).isEqualTo(validierungsstatus.name());
            Assertions.assertThat(result.schnellmeldung().validierungsstatus().name()).isEqualTo(validierungsstatus.name());
        }
    }

    @Nested
    class ToModel {
        @Test
        void should_return_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnModel_when_givenDTO() {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val now = LocalDateTime.now();
            val afterNow = now.plusDays(1);
            val dtoToMap = new StatusDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), new MeldungDTO(ValidierungsstatusDTO.VALIDE, true, true, now),
                    new MeldungDTO(ValidierungsstatusDTO.NICHT_VALIDIERT, false, false, afterNow));

            val result = unitUnderTest.toModel(dtoToMap);

            val expectedResult = new StatusModel(new BezirkUndWahlID(wahlID, wahlbezirkID), new MeldungModel(ValidierungsstatusModel.VALIDE, true, true, now),
                    new MeldungModel(ValidierungsstatusModel.NICHT_VALIDIERT, false, false, afterNow));
            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @EnumSource(ValidierungsstatusDTO.class)
        void should_mapToEnumWithSameName_when_givenModelValidierungsstatusEnumValue(final ValidierungsstatusDTO validierungsstatus) {
            val dtoToMap = new StatusDTO(null, new MeldungDTO(validierungsstatus, false, null, null),
                    new MeldungDTO(validierungsstatus, false, null, null));

            val result = unitUnderTest.toModel(dtoToMap);

            Assertions.assertThat(result.niederschrift().validierungsstatus().name()).isEqualTo(validierungsstatus.name());
            Assertions.assertThat(result.schnellmeldung().validierungsstatus().name()).isEqualTo(validierungsstatus.name());
        }
    }
}