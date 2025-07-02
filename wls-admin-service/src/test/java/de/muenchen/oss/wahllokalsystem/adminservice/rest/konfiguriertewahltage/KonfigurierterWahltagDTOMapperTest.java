package de.muenchen.oss.wahllokalsystem.adminservice.rest.konfiguriertewahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurierterWahltagDTOMapperTest {

    private final KonfigurierterWahltagDTOMapper unitUnderTest = Mappers.getMapper(KonfigurierterWahltagDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_mapToDtoWithEnumStatusAktiv_when_givenModelWithActiveTrue() {
            val dateNow = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", true, "123");

            Assertions.assertThat(konfigurierterWahltagModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toDTO(konfigurierterWahltagModel);

            val expectedkonfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "wahltagID", WahltagStatusDTO.AKTIV, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagDTO);
        }

        @Test
        void should_mapToDtoWithEnumStatusInaktiv_when_givenModelWithActiveFalse() {
            val dateNow = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", false, "123");

            Assertions.assertThat(konfigurierterWahltagModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toDTO(konfigurierterWahltagModel);

            val expectedkonfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "wahltagID", WahltagStatusDTO.INAKTIV, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagDTO);
        }
    }

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_mapToModelWithActiveTrue_when_givenDtoWithEnumStatusAktiv() {
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "wahltagID", WahltagStatusDTO.AKTIV, "123");

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(konfigurierterWahltagDTO);

            val expectedkonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", true, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagModel);
        }

        @Test
        void should_mapToModelWithActiveFalse_when_givenDtoWithEnumStatusInaktiv() {
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO(LocalDate.now(), "wahltagID", WahltagStatusDTO.INAKTIV, "123");

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(konfigurierterWahltagDTO);

            val expectedkonfigurierterWahltagModel = new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID", false, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagModel);
        }
    }
}
