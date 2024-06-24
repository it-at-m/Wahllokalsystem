package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlagModel;
import java.util.HashSet;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class WahlvorschlaegeDTOMapperTest {

    private final WahlvorschlaegeDTOMapper unitUnderTest = Mappers.getMapper(WahlvorschlaegeDTOMapper.class);

    @Nested
    class FromWahlvorschlagModelToWLSDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromWahlvorschlagModelToWLSDTO(null)).isNull();
        }

        @Test
        void isMappedToDTO() {
            val modelInput = createWahlvorschlagModel(1L);
            val dtoExpected = createWahlvorschlagDTO(1L);

            val result = unitUnderTest.fromWahlvorschlagModelToWLSDTO(modelInput);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }
    }

    @Nested
    class FromSetOfModelsToWLSWahlvorschlagDTOSet {
        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromSetOfModelsToWLSWahlvorschlagDTOSet(null)).isNull();
        }

        @Test
        void isMappedToSetOfDTOs() {
            Set<WahlvorschlagModel> setOfWahlvorschlagModelsInput = new HashSet<>();
            Set<WahlvorschlagDTO> setOfWahlvorschlagDTOsExpected = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                setOfWahlvorschlagModelsInput.add(createWahlvorschlagModel((long) i));
                setOfWahlvorschlagDTOsExpected.add(createWahlvorschlagDTO((long) i));
            }

            val result = unitUnderTest.fromSetOfModelsToWLSWahlvorschlagDTOSet(setOfWahlvorschlagModelsInput);
            Assertions.assertThat(result).isEqualTo(setOfWahlvorschlagDTOsExpected);
        }

    }

    @Nested
    class FromWahlvorschlaegeModelToWLSDTO {

        @Test
        void nullInNullOut() {
            Assertions.assertThat(unitUnderTest.fromWahlvorschlaegeModelToWLSDTO(null)).isNull();
        }

        @Test
        void isMappedToWahlvorschlaegeDTO() {
            val modelInput = createWahlvorschlaegeModel("wahlID","wahlbezirkID","stimmzettelgebietID", 1L);
            val dtoExpected = createWahlvorschlaegeDTO("wahlID","wahlbezirkID","stimmzettelgebietID", 1L);

            val result = unitUnderTest.fromWahlvorschlaegeModelToWLSDTO(modelInput);
            Assertions.assertThat(result).isEqualTo(dtoExpected);
        }

    }

    private WahlvorschlaegeModel createWahlvorschlaegeModel(String wahlID, String wahlbezirkID, String stimmzettelgebietID, Long index) {
        Set<WahlvorschlagModel> setOfWahlvorschlagModel = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            String identifikatorWahlvorschlag = "_wahlvorschlag_" + index + "_" + i;
            val wahlvorschlagModel = new WahlvorschlagModel(identifikatorWahlvorschlag,
                    index + i,
                    "kurzname_" + index + "_" + i,
                    (i == 0),
                    createSetOfKandidatModel(identifikatorWahlvorschlag));
            setOfWahlvorschlagModel.add(wahlvorschlagModel);
        }
        return new WahlvorschlaegeModel(wahlID, wahlbezirkID, stimmzettelgebietID, setOfWahlvorschlagModel);
    }

    private WahlvorschlagModel createWahlvorschlagModel(Long index) {
        String identifikatorWahlvorschlag = "_wahlvorschlag_" + index;
        return new WahlvorschlagModel(identifikatorWahlvorschlag,
                index,
                "kurzname_" + index,
                (index == 0),
                createSetOfKandidatModel(identifikatorWahlvorschlag));
    }

    private Set<KandidatModel> createSetOfKandidatModel(String praefixWahlvorschlag) {
        Set<KandidatModel> kandidatModels = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            String identifikatorKandidat = "_kandidat_" + praefixWahlvorschlag + "_" + i;
            val kandidatModel = new KandidatModel(identifikatorKandidat,
                    "KandName_" + identifikatorKandidat,
                    (long) i,
                    (i == 0),
                    (long) (i + 1),
                    (i % 2 == 0)

            );
            kandidatModels.add(kandidatModel);
        }
        return kandidatModels;
    }

    private WahlvorschlaegeDTO createWahlvorschlaegeDTO(String wahlID, String wahlbezirkID, String stimmzettelgebietID, Long index) {
        Set<WahlvorschlagDTO> setOfWahlvorschlagDTO = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            String identifikatorWahlvorschlag = "_wahlvorschlag_" + index + "_" + i;
            val wahlvorschlagDTO = new WahlvorschlagDTO(identifikatorWahlvorschlag,
                    index + i,
                    "kurzname_" + index + "_" + i,
                    (i == 0),
                    createSetOfKandidatDTO(identifikatorWahlvorschlag));
            setOfWahlvorschlagDTO.add(wahlvorschlagDTO);
        }
        return new WahlvorschlaegeDTO(wahlID, wahlbezirkID, stimmzettelgebietID, setOfWahlvorschlagDTO);
    }

    private WahlvorschlagDTO createWahlvorschlagDTO(Long index) {
        String identifikatorWahlvorschlag = "_wahlvorschlag_" + index;
        return new WahlvorschlagDTO(identifikatorWahlvorschlag,
                index,
                "kurzname_" + index,
                (index == 0),
                createSetOfKandidatDTO(identifikatorWahlvorschlag));
    }

    private Set<KandidatDTO> createSetOfKandidatDTO(String praefixWahlvorschlag) {
        Set<KandidatDTO> kandidatDTOs = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            String identifikatorKandidat = "_kandidat_" + praefixWahlvorschlag + "_" + i;
            val kandidatDTO = new KandidatDTO(identifikatorKandidat,
                    "KandName_" + identifikatorKandidat,
                    (long) i,
                    (i == 0),
                    (long) (i + 1),
                    (i % 2 == 0)

            );
            kandidatDTOs.add(kandidatDTO);
        }
        return kandidatDTOs;
    }
}