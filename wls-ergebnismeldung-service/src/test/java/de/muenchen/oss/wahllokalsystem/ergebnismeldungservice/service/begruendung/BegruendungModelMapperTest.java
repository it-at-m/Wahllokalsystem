package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.begruendung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Begruendung;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.Stapelart;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class BegruendungModelMapperTest {

    private final BegruendungModelMapper unitUnderTest = Mappers.getMapper(BegruendungModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnBegruendungModel_when_givenBegruendungEntity() {
            val begruendung = new Begruendung(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), "grund1", "grund2", true, true);
            val result = unitUnderTest.toModel(begruendung);

            val expectedResult = new BegruendungModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, "grund1", "grund2", true, true);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
    @Nested
    class ToEntity {
        @Test
        void should_returnBegruendungEntity_when_givenBegruendungModel() {
            val begruendungModel = new BegruendungModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, "grund1", "grund2", true, true);
            val result = unitUnderTest.toEntity(begruendungModel);

            val expectedResult = new Begruendung(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), "grund1", "grund2", true, true);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
