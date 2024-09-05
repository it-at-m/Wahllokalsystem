package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.MockDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class WahlbezirkModelMapperTest {

    private final WahlbezirkModelMapper unitUnderTest = Mappers.getMapper(WahlbezirkModelMapper.class);
    private final WahlModelMapper wahlModelMapper = Mappers.getMapper(WahlModelMapper.class);

    @Autowired
    ExceptionFactory exceptionFactory;

    @Test
    void fromListOfWahlbezirkModelToListOfWahlbezirkEntities() {
        val modelsToMap = MockDataFactory.createListOfWahlbezirkModel("", LocalDate.now());

        val result = unitUnderTest.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(modelsToMap);

        val expectedResult = MockDataFactory.createListOfWahlbezirkEntity("", LocalDate.now());
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void fromListOfWahlbezirkEntityToListOfWahlbezirkModel() {
        val entitiesToMap = MockDataFactory.createListOfWahlbezirkEntity("", LocalDate.now());

        val result = unitUnderTest.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(entitiesToMap);

        val expectedResult = MockDataFactory.createListOfWahlbezirkModel("", LocalDate.now());
        Assertions.assertThat(result).isEqualTo(expectedResult);
    }

    @Nested
    class ToWahlbezirkModelListMergedWithWahlenInfo {

        @Test
        void throwsFachlicheWlsExceptionIfNullRemoteWahlbezirke() {
            val wahlModels = MockDataFactory.createWahlModelList("", LocalDate.now());
            Assertions.assertThatCode(() -> unitUnderTest.toWahlbezirkModelListMergedWithWahlenInfo(null, wahlModels, exceptionFactory))
                    .isInstanceOf(FachlicheWlsException.class);
        }

        @Test
        void doesNotThrowExceptionIfRemoteWahlbezirkeIsSizeZero() {
            val setOfModels = new HashSet<WahlbezirkModel>();
            val wahlModels = MockDataFactory.createWahlModelList("", LocalDate.now());

            Assertions.assertThat(setOfModels).isEmpty();
            Assertions.assertThatCode(() -> unitUnderTest.toWahlbezirkModelListMergedWithWahlenInfo(setOfModels, wahlModels, exceptionFactory))
                    .doesNotThrowAnyException();
        }

        @Test
        void isMappingAndMergingCorrectlyWithWahlen() {
            val modelsToMap = MockDataFactory.createSetOfWahlbezirkModel("", LocalDate.now());
            List<Wahl> wahlen = MockDataFactory.createWahlEntityList();
            val aTestWahlID = "aRandomIDThatShouldBeTakenIntoWahlbezirkeToMergeWith";
            wahlen.forEach((wahl) -> wahl.setWahlID(aTestWahlID));
            val wahlModels = wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlen);

            val result = unitUnderTest.toWahlbezirkModelListMergedWithWahlenInfo(modelsToMap, wahlModels, exceptionFactory);

            Assertions.assertThat(result).allMatch((mergedWahlbezirk) -> mergedWahlbezirk.wahlID().equals(aTestWahlID));
        }
    }
}
