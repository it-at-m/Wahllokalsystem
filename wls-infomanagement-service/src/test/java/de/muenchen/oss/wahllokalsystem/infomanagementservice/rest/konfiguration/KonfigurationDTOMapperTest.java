package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.KonfigurationModel;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class KonfigurationDTOMapperTest {

    private final KonfigurationDTOMapper unitUnderTest = Mappers.getMapper(KonfigurationDTOMapper.class);

    @Test
    void toDTO() {
        val key = "key";
        val value = "value";
        val description = "description";
        val defaultValue = "defaultValue";

        val modelToMap = new KonfigurationModel(key, value, description, defaultValue);
        val expectedDTO = new KonfigurationDTO(key, value, description, defaultValue);

        val result = unitUnderTest.toDTO(modelToMap);

        Assertions.assertThat(result).isEqualTo(expectedDTO);
    }

    @Nested
    class ToModelKey {

        @ParameterizedTest
        @EnumSource(KonfigurationKey.class)
        void allPossibleInputKeysAreMappedToNonNull(final KonfigurationKey key) {
            Assertions.assertThat(unitUnderTest.toModelKey(key)).isNotNull();
        }

        @ParameterizedTest
        @EnumSource(KonfigurationKey.class)
        void isMappedToEqualsName(final KonfigurationKey key) {
            val parametersName = key.name();
            val mappedValuesName = unitUnderTest.toModelKey(key).name();

            Assertions.assertThat(parametersName).isEqualTo(mappedValuesName);
        }
    }

}