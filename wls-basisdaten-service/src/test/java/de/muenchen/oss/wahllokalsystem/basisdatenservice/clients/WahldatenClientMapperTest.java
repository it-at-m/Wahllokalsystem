package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { WahldatenClientMapperImpl.class, WahlbezirkeClientMapperImpl.class, WahlenClientMapperImpl.class })
class WahldatenClientMapperTest {

    @Autowired
    private WahldatenClientMapper unitUnderTest;

    @Nested
    class FromRemoteClientDTOToModel {

        @Test
        void isMapped() {

            val basisdatenDTO = MockDataFactory.createClientBasisdatenDTO(LocalDate.now());

            Assertions.assertThat(basisdatenDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientDTOToModel(basisdatenDTO);

            val expectedBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());

            Assertions.assertThat(result.basisstrukturdaten()).containsExactlyInAnyOrderElementsOf(expectedBasisdaten.basisstrukturdaten());
            Assertions.assertThat(result.wahlen()).usingRecursiveComparison().ignoringCollectionOrder()
                    .ignoringFields("reihenfolge", "waehlerverzeichnisnummer", "farbe").isEqualTo(expectedBasisdaten.wahlen());
            Assertions.assertThat(result.wahlbezirke()).containsExactlyInAnyOrderElementsOf(expectedBasisdaten.wahlbezirke());
            Assertions.assertThat(result.stimmzettelgebiete()).containsExactlyInAnyOrderElementsOf(expectedBasisdaten.stimmzettelgebiete());
        }
    }
}
