package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ErgebnisseDTOMapperTest {

    private final ErgebnisseDTOMapper unitUnderTest = Mappers.getMapper(ErgebnisseDTOMapper.class);

    @Nested
    class ToModel {

        @Nested
        class ToErgebnisseModel {

            @Test
            void should_returnNull_when_givenNull() {
                Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
            }

            @Test
            void should_returnErgebnisseModel_when_givenErgebnisseDTO() {
                val ergebnisseDTO = new ErgebnisseDTO(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), Collections.emptyList());
                val result = unitUnderTest.toModel(ergebnisseDTO);

                val expectedResult = new ErgebnisseModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, Collections.emptyList());

                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }

        @Nested
        class ToReferenceModel {

            @Test
            void should_returnNull_when_givenNull() {
                Assertions.assertThat(unitUnderTest.toReferenceModel(null, null, null)).isNull();
            }

            @Test
            void should_returnBegruendungReference_when_givenIDs() {
                val result = unitUnderTest.toReferenceModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A);

                val expectedResult = new ErgebnisseReference("bezirkID", "wahlID", Stapelart.LTW_BZW_A);

                Assertions.assertThat(result).isEqualTo(expectedResult);
            }
        }
    }

    @Nested
    class ToDTO {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toDTO(null)).isNull();
        }

        @Test
        void should_returnErgebnisseDTO_when_givenBegruendungModel() {
            val ergebnisseModel = new ErgebnisseModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, Collections.emptyList());
            val result = unitUnderTest.toDTO(ergebnisseModel);

            val expectedResult = new ErgebnisseDTO(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), Collections.emptyList());

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
