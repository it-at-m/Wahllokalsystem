package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlagModel;
import java.util.HashSet;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class WahlvorschlaegeControllerTest {

    @Mock
    WahlvorschlaegeService wahlvorschlaegeService;

    @Mock
    WahlvorschlaegeDTOMapper wahlvorschlaegeDTOMapper;

    @InjectMocks
    WahlvorschlaegeController wahlvorschlaegeController;

    @Test
    void getDataFromService() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val stimmzettelgebietID = "stimmzettelgebietID";

        val wahlvorschlaegeModel = createWahlvorschlaegeModel(wahlID, wahlbezirkID, stimmzettelgebietID, 1L);
        val wahlvorschlaegeDTO = createWahlvorschlaegeDTO(wahlID, wahlbezirkID, stimmzettelgebietID, 1L);

        Mockito.when(wahlvorschlaegeService.getWahlvorschlaege(wahlID, wahlbezirkID)).thenReturn(wahlvorschlaegeModel);
        Mockito.when(wahlvorschlaegeDTOMapper.fromWahlvorschlaegeModelToWLSDTO(wahlvorschlaegeModel)).thenReturn(wahlvorschlaegeDTO);

        val result = wahlvorschlaegeController.getWahlvorschlaege(wahlID, wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(result.getBody()).isEqualTo(wahlvorschlaegeDTO);
    }

    @Test
    void gotNoDataFromService() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";

        Mockito.when(wahlvorschlaegeService.getWahlvorschlaege(wahlID, wahlbezirkID)).thenReturn(null);

        val result = wahlvorschlaegeController.getWahlvorschlaege(wahlID, wahlbezirkID);

        Assertions.assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(result.getBody()).isNull();
    }

    @Test
    void requestIsMappedAndSendToService() {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        Assertions.assertThatNoException().isThrownBy(() -> wahlvorschlaegeController.getWahlvorschlaege(wahlID, wahlbezirkID));
    }

    private WahlvorschlaegeModel createWahlvorschlaegeModel(String wahlID, String wahlbezirkID, String stimmzettelgebietID, Long index) {
        Set<WahlvorschlagModel> setOfWahlvorschlagModel = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            String identifikatorWahlvorschlag = "_wahlvorschlag_" + index + "_" + i;
            val wahlvorschlagModel = new WahlvorschlagModel(identifikatorWahlvorschlag,
                    index + i,
                    "kurzname_" + index + "_" + i,
                    false,
                    createSetOfKandidatModel(identifikatorWahlvorschlag));
            setOfWahlvorschlagModel.add(wahlvorschlagModel);
        }
        return new WahlvorschlaegeModel(wahlID, wahlbezirkID, stimmzettelgebietID, setOfWahlvorschlagModel);
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
