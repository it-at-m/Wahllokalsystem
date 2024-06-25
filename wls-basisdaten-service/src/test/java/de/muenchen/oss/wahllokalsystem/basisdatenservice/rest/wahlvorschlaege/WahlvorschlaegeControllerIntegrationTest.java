package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlaege;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Kandidat;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlvorschlag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege.WahlvorschlaegeValidator;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.WithMockUserAsJwt;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.HashSet;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
public class WahlvorschlaegeControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlvorschlaegeDTOMapper dtoMapper;

    @Autowired
    WahlvorschlaegeModelMapper modelMapper;

    @Autowired
    WahlvorschlaegeRepository wahlvorschlaegeRepository;

    @SpyBean
    WahlvorschlaegeValidator wahlvorschlaegeValidator;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE);
        wahlvorschlaegeRepository.deleteAll();
    }

    @Nested
    class GetWahlvorschlaege {

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_WAHLVORSCHLAEGE, Authorities.REPOSITORY_READ_WAHLVORSCHLAEGE,
                        Authorities.REPOSITORY_WRITE_WAHLVORSCHLAEGE }
        )
        void emptyResponse() throws Exception {
            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorschlaege/wahlID1/wahlbezirkID1");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUserAsJwt(
                authorities = { Authorities.SERVICE_GET_WAHLVORSCHLAEGE, Authorities.REPOSITORY_READ_WAHLVORSCHLAEGE,
                        Authorities.REPOSITORY_WRITE_WAHLVORSCHLAEGE }
        )
        void dataFound() throws Exception {
            val wahlvorschlaege1 = createWahlvorschlaege("wahlID1", "wahlbezirkID1", "stimmzettelgebietID1", 1L);
            val wahlvorschlaege2 = createWahlvorschlaege("wahlID2", "wahlbezirkID2", "stimmzettelgebietID2", 2L);
            val wahlvorschlaegeToFind = createWahlvorschlaege("wahlIDToFind", "wahlbezirkID1", "stimmzettelgebietIDToFind", 1L);

            wahlvorschlaegeRepository.save(wahlvorschlaege1);
            wahlvorschlaegeRepository.save(wahlvorschlaegeToFind);
            wahlvorschlaegeRepository.save(wahlvorschlaege2);

            val request = MockMvcRequestBuilders.get("/businessActions/wahlvorschlaege/wahlIDToFind/wahlbezirkID1");

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), WahlvorschlaegeDTO.class);

            val expectedResponseBody = new WahlvorschlaegeDTO(
                    wahlvorschlaegeToFind.getBezirkUndWahlID().getWahlID(),
                    wahlvorschlaegeToFind.getBezirkUndWahlID().getWahlbezirkID(),
                    wahlvorschlaegeToFind.getStimmzettelgebietID(),
                    dtoMapper.fromSetOfModelsToWLSWahlvorschlagDTOSet(
                            modelMapper.fromSetOfWahlvorschlagEntityToSetOfModel(
                                    wahlvorschlaegeToFind.getWahlvorschlaege())));

            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }

    private Wahlvorschlaege createWahlvorschlaege(String wahlID, String wahlbezirkID, String stimmzettelgebietID, Long index) {
        Set<Wahlvorschlag> setOfWahlvorschlag = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            String identifikatorWahlvorschlag = "_wahlvorschlag_" + index + "_" + i;
            val wahlvorschlag = new Wahlvorschlag(identifikatorWahlvorschlag,
                    index + i,
                    "kurzname_" + index + "_" + i,
                    (i == 0),
                    createSetOfKandidat(identifikatorWahlvorschlag));
            setOfWahlvorschlag.add(wahlvorschlag);
        }
        return new Wahlvorschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmzettelgebietID, setOfWahlvorschlag);
    }

    private Set<Kandidat> createSetOfKandidat(String praefixWahlvorschlag) {
        Set<Kandidat> kandidats = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            String identifikatorKandidat = "_kandidat_" + praefixWahlvorschlag + "_" + i;
            val kandidat = new Kandidat(identifikatorKandidat,
                    "KandName_" + identifikatorKandidat,
                    (long) i,
                    (i == 0),
                    (long) (i + 1),
                    (i % 2 == 0)

            );
            kandidats.add(kandidat);
        }
        return kandidats;
    }

}
