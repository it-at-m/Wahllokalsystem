package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurierterWahltagClientMapperTest {

    private final KonfigurierterWahltagClientMapper unitUnderTest = Mappers.getMapper(KonfigurierterWahltagClientMapper.class);

    @Nested
    class FromRemoteClientDTOToModel {

        @Test
        void isMappedWithStatusAktive() {
            val konfigurierterWahltagDTO = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1),
                    KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientDTOToModel(konfigurierterWahltagDTO);

            val expectedKonfigurieterWahltag = MockDataFactory.createClientKonfigurierterWahltagModel(LocalDate.now().plusMonths(1), true);

            Assertions.assertThat(result).isEqualTo(expectedKonfigurieterWahltag);
        }

        @Test
        void isMappedWithStatusInaktive() {
            val konfigurierterWahltagDTO = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1),
                    KonfigurierterWahltagDTO.WahltagStatusEnum.INAKTIV);

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientDTOToModel(konfigurierterWahltagDTO);

            val expectedKonfigurieterWahltag = MockDataFactory.createClientKonfigurierterWahltagModel(LocalDate.now().plusMonths(1), false);

            Assertions.assertThat(result).isEqualTo(expectedKonfigurieterWahltag);
        }
    }
}
