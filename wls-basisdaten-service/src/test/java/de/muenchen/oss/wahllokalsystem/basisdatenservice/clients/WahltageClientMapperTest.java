package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahltageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahltageClientMapperTest {

    private final WahltageClientMapper unitUnderTest = Mappers.getMapper(WahltageClientMapper.class);

    @Nested
    class ToWahltagModel {

        @Test
        void isMapped() {
            val dtoToMap = new WahltagDTO();
            dtoToMap.setIdentifikator("identifikatorWahltag1");
            dtoToMap.setBeschreibung("beschreibungWahltag1");
            dtoToMap.setNummer("nummerWahltag1");
            dtoToMap.setTag(LocalDate.now().minusMonths(2));

            Assertions.assertThat(dtoToMap).hasNoNullFieldsOrProperties();

            val result = unitUnderTest.toWahltagModel(dtoToMap);

            val expectedWahltag = new WahltagModel(
                    "identifikatorWahltag1",
                    LocalDate.now().minusMonths(2),
                    "beschreibungWahltag1",
                    "nummerWahltag1");

            Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expectedWahltag);
        }

    }

    @Nested
    class FromRemoteClientWahltageDTOtoListOfWahltagModel {

        @Test
        void isMapped() {
            val dtoToMap = new WahltageDTO();

            val wahltag1 = new WahltagDTO();
            wahltag1.setIdentifikator("identifikatorWahltag1");
            wahltag1.setBeschreibung("beschreibungWahltag1");
            wahltag1.setNummer("nummerWahltag1");
            wahltag1.setTag(LocalDate.now().minusMonths(2));

            val wahltag2 = new WahltagDTO();
            wahltag2.setIdentifikator("identifikatorWahltag2");
            wahltag2.setBeschreibung("beschreibungWahltag2");
            wahltag2.setNummer("nummerWahltag2");
            wahltag2.setTag(LocalDate.now().minusMonths(1));

            val wahltag3 = new WahltagDTO();
            wahltag3.setIdentifikator("identifikatorWahltag3");
            wahltag3.setBeschreibung("beschreibungWahltag3");
            wahltag3.setNummer("nummerWahltag3");
            wahltag3.setTag(LocalDate.now().plusMonths(1));

            dtoToMap.setWahltage(Set.of(
                    wahltag1, wahltag2, wahltag3));

            Assertions.assertThat(dtoToMap).hasNoNullFieldsOrProperties();
            Assertions.assertThat(dtoToMap.getWahltage()).size().isEqualTo(3);

            val result = unitUnderTest.fromRemoteClientWahltageDTOtoListOfWahltagModel(dtoToMap);

            val expectedWahltage = List.of(
                    new WahltagModel("identifikatorWahltag1",
                            LocalDate.now().minusMonths(2),
                            "beschreibungWahltag1",
                            "nummerWahltag1"),
                    new WahltagModel("identifikatorWahltag2",
                            LocalDate.now().minusMonths(1),
                            "beschreibungWahltag2",
                            "nummerWahltag2"),
                    new WahltagModel("identifikatorWahltag3",
                            LocalDate.now().plusMonths(1),
                            "beschreibungWahltag3",
                            "nummerWahltag3"));

            Assertions.assertThat(result).containsExactlyInAnyOrderElementsOf(expectedWahltage);
        }
    }
}
