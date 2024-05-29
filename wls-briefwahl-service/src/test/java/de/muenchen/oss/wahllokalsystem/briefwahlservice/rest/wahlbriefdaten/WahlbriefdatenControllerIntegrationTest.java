package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.wahlbriefdaten;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.Wahlbriefdaten;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.WahlbriefdatenRepository;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.service.wahlbriefdaten.WahlbriefdatenModelMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.time.LocalDateTime;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, Profiles.NO_BEZIRKS_ID_CHECK })
public class WahlbriefdatenControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WahlbriefdatenRepository wahlbriefdatenRepository;

    @Autowired
    WahlbriefdatenModelMapper wahlbriefdatenModelMapper;

    @Autowired
    WahlbriefdatenDTOMapper wahlbriefdatenDTOMapper;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs("", "", Authorities.REPOSITORY_DELETE_WAHLBRIEFDATEN);
        wahlbriefdatenRepository.deleteAll();
    }

    @Nested
    class GetWahlbriefdaten {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAHLBRIEFDATEN })
        void noDataFound() throws Exception {
            val request = get("/businessActions/wahlbriefdaten/wahlbezirkID");

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAHLBRIEFDATEN, Authorities.REPOSITORY_WRITE_WAHLBRIEFDATEN })
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "wahlbezirkID";
            val wahlbriefdaten1 = new Wahlbriefdaten("id1", null, null, null, null, null);
            val wahlbriefdatenToFind = new Wahlbriefdaten(wahlbezirkIDToFind, 1L, 2L, 3L, 4L, LocalDateTime.parse("2024-09-13T12:11:21.343"));
            val wahlbriefdaten2 = new Wahlbriefdaten("id1", null, null, null, null, null);

            wahlbriefdatenRepository.saveAll(List.of(wahlbriefdaten1, wahlbriefdatenToFind, wahlbriefdaten2));

            val request = get("/businessActions/wahlbriefdaten/" + wahlbezirkIDToFind);

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(), WahlbriefdatenDTO.class);

            val expectedResponseBody = wahlbriefdatenDTOMapper.toDTO(wahlbriefdatenModelMapper.toModel(wahlbriefdatenToFind));
            Assertions.assertThat(responseBody).isEqualTo(expectedResponseBody);
        }
    }

    @Nested
    class PostWahlbriefdaten {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_WAHLBRIEFDATEN, Authorities.REPOSITORY_WRITE_WAHLBRIEFDATEN })
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new WahlbriefdatenWriteDTO(1L, 2L, 3L, 4L, LocalDateTime.parse("2023-02-23T02:23:32.021"));

            val request = post("/businessActions/wahlbriefdaten/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));
            api.perform(request).andExpect(status().isOk());

            val entityFromRepo = wahlbriefdatenRepository.findById(wahlbezirkID).get();

            val expectedSavedEntity = wahlbriefdatenModelMapper.toEntity(wahlbriefdatenDTOMapper.toModel(wahlbezirkID, requestBody));
            Assertions.assertThat(entityFromRepo).isEqualTo(expectedSavedEntity);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_WAHLBRIEFDATEN, Authorities.REPOSITORY_WRITE_WAHLBRIEFDATEN })
        void existingDataIsReplaced() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = new WahlbriefdatenWriteDTO(1L, 2L, 3L, 4L, LocalDateTime.parse("2035-02-27T00:01:02.003"));

            val wahlbriefdatenToReplace = new Wahlbriefdaten(wahlbezirkID, 11L, 22L, 33L, 44L, LocalDateTime.now());
            wahlbriefdatenRepository.save(wahlbriefdatenToReplace);

            val request = post("/businessActions/wahlbriefdaten/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));
            api.perform(request).andExpect(status().isOk());

            val entityFromRepo = wahlbriefdatenRepository.findById(wahlbezirkID).get();

            val expectedSavedEntity = wahlbriefdatenModelMapper.toEntity(wahlbriefdatenDTOMapper.toModel(wahlbezirkID, requestBody));
            Assertions.assertThat(entityFromRepo).isEqualTo(expectedSavedEntity);
        }
    }
}
