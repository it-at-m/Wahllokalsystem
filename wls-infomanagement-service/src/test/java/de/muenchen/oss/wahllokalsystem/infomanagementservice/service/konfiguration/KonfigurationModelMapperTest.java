package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.konfiguration.Konfiguration;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class KonfigurationModelMapperTest {

    private final KonfigurationModelMapper unitUnderTest = Mappers.getMapper(KonfigurationModelMapper.class);

    @Nested
    class ToModel {
        @Test
        void entityIsMapped() {
            val key = "key";
            val value = "value";
            val description = "description";
            val defaultValue = "defaultValue";
            val entityToMap = new Konfiguration(key, value, description, defaultValue);

            val expectedResult = new KonfigurationModel(key, value, description, defaultValue);

            val result = unitUnderTest.toModel(entityToMap);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class GetAlternativKey {

        @Test
        void alternativKeyWasFound() {
            Assertions.assertThat(unitUnderTest.getAlternativKey(KonfigurationKonfigKey.FRUEHESTE_EROEFFNUNGSZEIT, WahlbezirkArt.BWB).get()).isNotNull();
        }

        @Test
        void noAlternativKeyWasFound() {
            Assertions.assertThat(unitUnderTest.getAlternativKey(KonfigurationKonfigKey.WILLKOMMENSTEXT, WahlbezirkArt.BWB)).isEmpty();
        }
    }

    @Nested
    class ToEntity {

        @Test
        void modelIsMappedToEntity() {
            val schluessel = "schluessel";
            val wert = "wert";
            val beschreibung = "beschreibung";
            val standardwert = "standardwert";
            val modelToMap = new KonfigurationSetModel(schluessel, wert, beschreibung, standardwert);
            val expectedResult = new Konfiguration(schluessel, wert, beschreibung, standardwert);

            val result = unitUnderTest.toEntity(modelToMap);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

    }

    @Nested
    class MapStandardwertFromModel {

        @Test
        void mapExistingStandardwert() {
            val wert = "wert";
            val standardwert = "standardwert";
            val modelToMap = KonfigurationSetModel.builder().standardwert(standardwert).wert(wert).build();

            val result = unitUnderTest.mapStandardwertFromModel(modelToMap);

            Assertions.assertThat(result).isEqualTo(standardwert);
        }

        @Test
        void mapFallbackValueWhenStandardwertIsNull() {
            val wert = "wert";
            final String standardwert = null;
            val modelToMap = KonfigurationSetModel.builder().standardwert(standardwert).wert(wert).build();

            val result = unitUnderTest.mapStandardwertFromModel(modelToMap);

            Assertions.assertThat(result).isEqualTo(wert);
        }
    }

}
