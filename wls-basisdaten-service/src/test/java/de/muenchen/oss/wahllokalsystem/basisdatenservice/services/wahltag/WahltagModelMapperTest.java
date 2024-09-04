package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahltagModelMapperTest {

    private final WahltagModelMapper unitUnderTest = Mappers.getMapper(WahltagModelMapper.class);

    @Test
    void fromWahltagModelToWahltagEntityList() {
        val modelsToMap = createWahltagModelList();

        val result = unitUnderTest.fromWahltagModelToWahltagEntityList(modelsToMap);

        val expectedResult = createWahltagList();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void fromWahltagEntityToWahltagModelList() {
        val entitiesToMap = createWahltagList();

        val result = unitUnderTest.fromWahltagEntityToWahltagModelList(entitiesToMap);

        val expectedResult = createWahltagModelList();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void toModel() {
        val wahltagID = "wahltagID";
        val wahltag = LocalDate.now();
        val beschreibung = "beschreibung";
        val nummer = "nummer";
        val entityToMap = new Wahltag(wahltagID, wahltag, beschreibung, nummer);

        val result = unitUnderTest.toModel(entityToMap);

        val expectedResult = new WahltagModel(wahltagID, wahltag, beschreibung, nummer);

        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    private List<Wahltag> createWahltagList() {
        val wahltag1 = new Wahltag("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new Wahltag("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new Wahltag("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }

    private List<WahltagModel> createWahltagModelList() {
        val wahltag1 = new WahltagModel("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new WahltagModel("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new WahltagModel("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        return List.of(wahltag1, wahltag2, wahltag3);
    }
}
