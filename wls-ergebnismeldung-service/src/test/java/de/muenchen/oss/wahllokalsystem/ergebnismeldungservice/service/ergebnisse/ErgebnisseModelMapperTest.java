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
            val ergebnis1 = new Ergebnis("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnis2 = new Ergebnis("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisList = new ArrayList<Ergebnis>();
            newErgebnisList.add(ergebnis1);
            newErgebnisList.add(ergebnis2);
            val ergebnisse = new Ergebnisse(new BezirkUndWahlIDStapelart("bezirkID", "wahlID", Stapelart.LTW_BZW_A), newErgebnisList);
            val result = unitUnderTest.toModel(ergebnisse);

            val ergebnisModel1 = new ErgebnisModel("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnisModel2 = new ErgebnisModel("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            newErgebnisModelList.add(ergebnisModel1);
            newErgebnisModelList.add(ergebnisModel2);

            val expectedResult = new ErgebnisseModel("bezirkID", "wahlID", StapelartModel.LTW_BZW_A, newErgebnisModelList);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }

    @Nested
    class ToEntity {

        @Test
        void should_returnErgebnisseEntity_when_givenErgebnisseModel() {
            val ergebnisModel1 = new ErgebnisModel("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnisModel2 = new ErgebnisModel("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            newErgebnisModelList.add(ergebnisModel1);
            newErgebnisModelList.add(ergebnisModel2);
            val ergebnisseModel = new ErgebnisseModel("bezirkID", "wahlID", StapelartModel.LTW_BZW_A, newErgebnisModelList);
            val result = unitUnderTest.toEntity(ergebnisseModel);

            val ergebnis1 = new Ergebnis("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnis2 = new Ergebnis("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisList = new ArrayList<Ergebnis>();
            newErgebnisList.add(ergebnis1);
            newErgebnisList.add(ergebnis2);

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
