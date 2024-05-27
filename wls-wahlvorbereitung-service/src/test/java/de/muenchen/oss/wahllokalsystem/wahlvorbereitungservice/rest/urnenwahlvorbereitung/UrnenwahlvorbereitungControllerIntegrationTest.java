package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitungRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten.UrnenwahlVorbereitungTestdatenfactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles({ TestConstants.SPRING_TEST_PROFILE, Profiles.NO_BEZIRKS_ID_CHECK })
public class UrnenwahlvorbereitungControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UrnenwahlvorbereitungModelMapper urnenwahlvorbereitungModelMapper;

    @Autowired
    UrnenwahlvorbereitungDTOMapper urnenwahlvorbereitungDTOMapper;

    @Autowired
    UrnenwahlVorbereitungRepository urnenwahlVorbereitungRepository;

    @AfterEach
    void tearDown() throws Exception {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_URNENWAHLVORBEREITUNG);
        urnenwahlVorbereitungRepository.deleteAll();
    }

    @Nested
    class GetUrnenwahlVorbereitung {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_URNENWAHLVORBEREITUNG })
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "wahlbezirkIDToFind";

            val wahlbezirk1 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk1", "wahlID1").build();
            val wahlbezirk2 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk2", "wahlID2").build();
            val wahlbezirkToFind = UrnenwahlVorbereitungTestdatenfactory.initValid(wahlbezirkIDToFind, "wahlID3").build();
            val wahlbezirk4 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk4", "wahlID4").build();
            urnenwahlVorbereitungRepository.saveAll(List.of(wahlbezirk1, wahlbezirk2, wahlbezirkToFind, wahlbezirk4));

            val request = get("/businessActions/urnenwahlVorbereitung/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UrnenwahlvorbereitungDTO.class);

            val expectedResponseBody = urnenwahlvorbereitungDTOMapper.toDTO(urnenwahlvorbereitungModelMapper.toModel(wahlbezirkToFind));

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_URNENWAHLVORBEREITUNG })
        void noDataFound() throws Exception {
            val wahlbezirkIDToLookup = "wahlbezirkIDToFind";

            val wahlbezirk1 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk1", "wahlID1").build();
            val wahlbezirk2 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk2", "wahlID2").build();
            val wahlbezirkToFind = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk3", "wahlID3").build();
            val wahlbezirk4 = UrnenwahlVorbereitungTestdatenfactory.initValid("wahlbezirk4", "wahlID4").build();
            urnenwahlVorbereitungRepository.saveAll(List.of(wahlbezirk1, wahlbezirk2, wahlbezirkToFind, wahlbezirk4));

            val request = get("/businessActions/urnenwahlVorbereitung/" + wahlbezirkIDToLookup);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }
}
