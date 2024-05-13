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

}
