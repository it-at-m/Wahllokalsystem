package de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenListeDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KennbuchstabenListenDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.konfiguration.dto.KonfigurationSetDTO;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListeModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenListenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KennbuchstabenModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationModel;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.service.konfiguration.model.KonfigurationSetModel;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

class KonfigurationDTOMapperTest {

    private final KonfigurationDTOMapper unitUnderTest = Mappers.getMapper(KonfigurationDTOMapper.class);

    @Nested
    class ToDTO {

        @Test
        void konfigurationModelIsMapped() {
            val key = "key";
            val value = "value";
            val description = "description";
            val defaultValue = "defaultValue";

            val modelToMap = new KonfigurationModel(key, value, description, defaultValue);
            val expectedDTO = new KonfigurationDTO(key, value, description, defaultValue);

            val result = unitUnderTest.toDTO(modelToMap);

            Assertions.assertThat(result).isEqualTo(expectedDTO);
        }

        @Test
        void KennbuchstabenListenModelIsMapped() {
            val kennbuchstaben11 = new KennbuchstabenModel(List.of("11a", "11b"));
            val kennbuchstaben12 = new KennbuchstabenModel(List.of("12"));
            val kennbuchstaben21 = new KennbuchstabenModel(List.of("21a", "21b", "21c"));
            val kennbuchstaben22 = new KennbuchstabenModel(List.of("22a", "22b"));

            val kennbuchstabenListe1 = new KennbuchstabenListeModel(List.of(kennbuchstaben11, kennbuchstaben12));
            val kennbuchstabenListe2 = new KennbuchstabenListeModel(List.of(kennbuchstaben21, kennbuchstaben22));

            val modelToMap = new KennbuchstabenListenModel(List.of(kennbuchstabenListe1, kennbuchstabenListe2));

            val result = unitUnderTest.toDTO(modelToMap);

            val listeDTO1 = new KennbuchstabenListeDTO(List.of(new KennbuchstabenDTO(List.of("11a", "11b")), new KennbuchstabenDTO(List.of("12"))));
            var listeDTO2 = new KennbuchstabenListeDTO(
                    List.of(new KennbuchstabenDTO(List.of("21a", "21b", "21c")), new KennbuchstabenDTO(List.of("22a", "22b"))));

            val expectedResult = new KennbuchstabenListenDTO(List.of(listeDTO1, listeDTO2));

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }

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

    @Nested
    class ToSetModel {
        @Test
        void isMappedToModel() {
            val dtoToMap = new KonfigurationSetDTO("wert", "beschreibung", "standard");
            val keyToMap = KonfigurationKey.FRUEHESTE_EROEFFNUNGSZEIT;

            val expectedResult = new KonfigurationSetModel("FRUEHESTE_EROEFFNUNGSZEIT", "wert", "beschreibung", "standard");

            val result = unitUnderTest.toSetModel(keyToMap, dtoToMap);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

}
