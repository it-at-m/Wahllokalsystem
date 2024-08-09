package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

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
        void isMapped() {
            val konfigurierterWahltagDTO = MockDataFactory.createClientKonfigurierterWahltagDTO(LocalDate.now().plusMonths(1));

            Assertions.assertThat(konfigurierterWahltagDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientDTOToModel(konfigurierterWahltagDTO);

            val expecteKonfigurieterWahltag = MockDataFactory.createClientKonfigurierterWahltagModel(LocalDate.now().plusMonths(1));

            Assertions.assertThat(result).isEqualTo(expecteKonfigurieterWahltag);
        }
    }
}
