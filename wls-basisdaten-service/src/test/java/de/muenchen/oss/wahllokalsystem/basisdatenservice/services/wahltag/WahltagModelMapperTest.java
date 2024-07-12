package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import static org.junit.jupiter.api.Assertions.*;
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
    void toEntity() {
        val modelToMap = createWahltagModel();

        val result = unitUnderTest.toEntity(modelToMap);

        val expectedResult = createWahltagEntity();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void toModel() {
        val entityToMap = createWahltagEntity();

        val result = unitUnderTest.toModel(entityToMap);

        val expectedResult = createWahltagModel();
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

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

    private Wahltag createWahltagEntity() {
        return new Wahltag("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
    }

    private WahltagModel createWahltagModel() {
        return new WahltagModel("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
    }

    private List<Wahltag> createWahltagList() {
        val wahltag1 = new Wahltag("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new Wahltag("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new Wahltag("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        val wahltagEntities = List.of(wahltag1, wahltag2, wahltag3);

        return wahltagEntities;
    }

    private List<WahltagModel> createWahltagModelList() {
        val wahltag1 = new WahltagModel("identifikatorWahltag1", LocalDate.now().minusMonths(2), "beschreibungWahltag1", "nummerWahltag1");
        val wahltag2 = new WahltagModel("identifikatorWahltag2", LocalDate.now().minusMonths(1), "beschreibungWahltag2", "nummerWahltag2");
        val wahltag3 = new WahltagModel("identifikatorWahltag3", LocalDate.now().plusMonths(1), "beschreibungWahltag3", "nummerWahltag3");

        val wahltagModels = List.of(wahltag1, wahltag2, wahltag3);

        return wahltagModels;
    }
}
