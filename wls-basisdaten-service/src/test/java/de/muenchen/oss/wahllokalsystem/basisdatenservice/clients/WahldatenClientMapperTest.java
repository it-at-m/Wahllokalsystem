package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahldatenClientMapperTest {

    private final WahldatenClientMapper unitUnderTest = Mappers.getMapper(WahldatenClientMapper.class);

    @Nested
    class WahlDTOToWahlModel {

        @Test
        void isMapped() {
            val aNowMoment = LocalDate.now();
            val wahl1 = new WahlDTO();
            wahl1.setIdentifikator("identifikatorWahl1");
            wahl1.setNummer("nummerWahl1");
            wahl1.setName("nameWahl1");
            wahl1.setWahlart(WahlDTO.WahlartEnum.BAW);
            wahl1.setWahltag(aNowMoment);

            Assertions.assertThat(wahl1).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.wahlDTOToWahlModel(wahl1);

            val expectedWahl = new WahlModel(
                    "identifikatorWahl1",
                    "nameWahl1",
                    0L,
                    0L,
                    aNowMoment,
                    Wahlart.BAW,
                    null,
                    "nummerWahl1");

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedWahl);
        }

    }

    @Nested
    class FromRemoteClientDTOToModel {

        @Test
        void isMapped() {

            val basisdatenDTO = MockDataFactory.createClientBasisdatenDTO(LocalDate.now());

            Assertions.assertThat(basisdatenDTO).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.fromRemoteClientDTOToModel(basisdatenDTO);

            val expecteBasisdaten = MockDataFactory.createBasisdatenModel(LocalDate.now());

            Assertions.assertThat(result.basisstrukturdaten()).containsExactlyInAnyOrderElementsOf(expecteBasisdaten.basisstrukturdaten());
            Assertions.assertThat(result.wahlen()).usingRecursiveComparison().ignoringCollectionOrder()
                    .ignoringFields("reihenfolge", "waehlerverzeichnisnummer", "farbe").isEqualTo(expecteBasisdaten.wahlen());
            Assertions.assertThat(result.wahlbezirke()).containsExactlyInAnyOrderElementsOf(expecteBasisdaten.wahlbezirke());
            Assertions.assertThat(result.stimmzettelgebiete()).containsExactlyInAnyOrderElementsOf(expecteBasisdaten.stimmzettelgebiete());
        }
    }
}
