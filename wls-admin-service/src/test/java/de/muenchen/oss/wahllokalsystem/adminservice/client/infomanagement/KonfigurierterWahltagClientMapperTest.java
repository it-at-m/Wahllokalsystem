package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurierterWahltagClientMapperTest {

    private final KonfigurierterWahltagClientMapper unitUnderTest = Mappers.getMapper(KonfigurierterWahltagClientMapper.class);

    @Nested
    class ToDto {

        @Test
        void should_returnNull_when_nullIsGiven() {
            Assertions.assertThat(unitUnderTest.toDto(null)).isNull();
        }

        @Test
        void should_mapToDtoWithEnumStatusAktiv_when_givenModelWithActiveTrue() {
            val dateNow = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", true, "123");

            Assertions.assertThat(konfigurierterWahltagModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toDto(konfigurierterWahltagModel);

            val expectedkonfigurierterWahltagDto = new KonfigurierterWahltagDTO().wahltag(dateNow).wahltagID("wahltagID").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagDto);
        }

        @Test
        void should_mapToDtoWithEnumStatusInaktiv_when_givenModelWithActiveFalse() {
            val dateNow = LocalDate.now();
            val konfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", false, "123");

            Assertions.assertThat(konfigurierterWahltagModel).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toDto(konfigurierterWahltagModel);

            val expectedkonfigurierterWahltagDto = new KonfigurierterWahltagDTO().wahltag(dateNow).wahltagID("wahltagID").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.INAKTIV).nummer("123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagDto);
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
            val dateNow = LocalDate.now();
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(dateNow).wahltagID("wahltagID").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV).nummer("123");

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(konfigurierterWahltagDTO);

            val expectedkonfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", true, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagModel);
        }

        @Test
        void should_mapToModelWithActiveFalse_when_givenDtoWithEnumStatusInaktiv() {
            val dateNow = LocalDate.now();
            val konfigurierterWahltagDTO = new KonfigurierterWahltagDTO().wahltag(dateNow).wahltagID("wahltagID").wahltagStatus(
                    KonfigurierterWahltagDTO.WahltagStatusEnum.INAKTIV).nummer("123");

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toModel(konfigurierterWahltagDTO);

            val expectedkonfigurierterWahltagModel = new KonfigurierterWahltagModel(dateNow, "wahltagID", false, "123");

            Assertions.assertThat(result).isEqualTo(expectedkonfigurierterWahltagModel);
        }
    }
}
