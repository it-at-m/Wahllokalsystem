package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.begruendung.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung.BegruendungReference;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BegruendungDTOMapperTest {

    private final BegruendungDTOMapper unitUnderTest = Mappers.getMapper(BegruendungDTOMapper.class);

    @Nested
    class ToModel {

        @Nested
        class ToBegruendungModel {

            @Test
            void should_returnNull_when_givenNull() {
                Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
            }

            @Test
            void should_returnBegruendungModel_when_givenBegruendungDTO() {
                val begruendungDTO = new BegruendungDTO(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), "grund1", "grund2", true,
                        true);
                val result = unitUnderTest.toModel(begruendungDTO);

                val expectedResult = new BegruendungModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, "grund1", "grund2", true, true);

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

                val expectedResult = new BegruendungReference("bezirkID", "wahlID", Stapelart.LTW_BZW_A);

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
        void should_returnBegruendungDTO_when_givenBegruendungModel() {
            val begruendungModel = new BegruendungModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, "grund1", "grund2", true, true);
            val result = unitUnderTest.toDTO(begruendungModel);

            val expectedResult = new BegruendungDTO(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), "grund1", "grund2", true, true);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
