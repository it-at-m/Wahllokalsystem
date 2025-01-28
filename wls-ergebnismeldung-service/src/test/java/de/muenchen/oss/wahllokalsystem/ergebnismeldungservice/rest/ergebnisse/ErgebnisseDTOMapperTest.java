package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.ergebnisse;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.common.StapelartDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.StapelartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnisse.ErgebnisseReference;
import java.util.ArrayList;
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
                val ergebnisModel1 = new ErgebnisModel("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
                val ergebnisModel2 = new ErgebnisModel("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
                val newErgebnisModelList = new ArrayList<ErgebnisModel>();
                newErgebnisModelList.add(ergebnisModel1);
                newErgebnisModelList.add(ergebnisModel2);

                val ergebnisDTO1 = new ErgebnisDTO("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
                val ergebnisDTO2 = new ErgebnisDTO("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
                val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
                newErgebnisDTOList.add(ergebnisDTO1);
                newErgebnisDTOList.add(ergebnisDTO2);

                val ergebnisseDTO = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO("bezirkID", "wahlID", StapelartDTO.LTW_BZW_A), newErgebnisDTOList);
                val result = unitUnderTest.toModel(ergebnisseDTO);

                val expectedResult = new ErgebnisseModel("bezirkID", "wahlID", StapelartModel.LTW_BZW_A, newErgebnisModelList);

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
                val result = unitUnderTest.toReferenceModel("bezirkID", "wahlID", StapelartDTO.LTW_BZW_A);

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
        void should_returnErgebnisseDTO_when_givenErgebnisseModel() {
            val ergebnisModel1 = new ErgebnisModel("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnisModel2 = new ErgebnisModel("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisModelList = new ArrayList<ErgebnisModel>();
            newErgebnisModelList.add(ergebnisModel1);
            newErgebnisModelList.add(ergebnisModel2);

            val ergebnisDTO1 = new ErgebnisDTO("wahlvorschlagID1", "kandidatID1", 1L, 1, 1L);
            val ergebnisDTO2 = new ErgebnisDTO("wahlvorschlagID2", "kandidatID2", 2L, 1, 2L);
            val newErgebnisDTOList = new ArrayList<ErgebnisDTO>();
            newErgebnisDTOList.add(ergebnisDTO1);
            newErgebnisDTOList.add(ergebnisDTO2);

            val ergebnisseModel = new ErgebnisseModel("bezirkID", "wahlID", StapelartModel.LTW_BZW_A, newErgebnisModelList);
            val result = unitUnderTest.toDTO(ergebnisseModel);

            val expectedResult = new ErgebnisseDTO(new BezirkUndWahlIDStapelartDTO("bezirkID", "wahlID", StapelartDTO.LTW_BZW_A), newErgebnisDTOList);

            Assertions.assertThat(result).isEqualTo(expectedResult);
        }
    }
}
