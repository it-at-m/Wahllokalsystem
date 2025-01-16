package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.BezirkUndWahlIDStapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnis;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import java.util.ArrayList;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class ErgebnisseModelMapperTest {

    private final ErgebnisseModelMapper unitUnderTest = Mappers.getMapper(ErgebnisseModelMapper.class);

    @Nested
    class ToModel {

        @Test
        void should_returnNull_when_givenNull() {
            Assertions.assertThat(unitUnderTest.toModel(null)).isNull();
        }

        @Test
        void should_returnErgebnisseModel_when_givenErgebnisseEntity() {
            val ergebnis1 = new Ergebnis(null, null, null, 1, null);
            val newErgebnisList = new ArrayList<Ergebnis>();
            newErgebnisList.add(ergebnis1);
            val ergebnisse = new Ergebnisse(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), newErgebnisList);
            val result = unitUnderTest.toModel(ergebnisse);

            val expectedResult = new ErgebnisseModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, newErgebnisList);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnErgebnisseEntity_when_givenErgebnisseModel() {
            val ergebnis1 = new Ergebnis(null, null, null, 1, null);
            val newErgebnisList = new ArrayList<Ergebnis>();
            newErgebnisList.add(ergebnis1);
            val ergebnisseModel = new ErgebnisseModel("bezirkID", "wahlID", Stapelart.LTW_BZW_A, newErgebnisList);
            val result = unitUnderTest.toEntity(ergebnisseModel);

            val expectedResult = new Ergebnisse(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), newErgebnisList);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEmbeddedId {

        @Test
        void should_returnEmbeddedId_when_givenErgebnisseReference() {
            val ergebnisseReference = new ErgebnisseReference("bezirkID", "wahlID", Stapelart.LTW_BZW_A);
            val result = unitUnderTest.toEmbeddedId(ergebnisseReference);

            val expectedResult = new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
