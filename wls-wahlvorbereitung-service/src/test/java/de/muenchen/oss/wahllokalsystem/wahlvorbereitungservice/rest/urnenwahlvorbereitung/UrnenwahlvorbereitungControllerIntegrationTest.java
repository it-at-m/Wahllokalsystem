package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlvorbereitung;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlVorbereitungRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlvorbereitung.UrnenwahlvorbereitungModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten.UrnenwahlVorbereitungTestdatenfactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.test.web.servlet.RequestBuilder;

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
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_URNENWAHLVORBEREITUNG);
        urnenwahlVorbereitungRepository.deleteAll();
    }

    @Nested
    class GetUrnenwahlVorbereitung {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG })
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
        @WithMockUser(authorities = { Authorities.SERVICE_GET_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG })
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

    @Nested
    class PostUrnenwahlvorbereitung {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_URNENWAHLVORBEREITUNG })
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val requestBody = UrnenwahlVorbereitungTestdatenfactory.initValidDTO("wahlID").build();
            val request = buildPostRequest(wahlbezirkID, requestBody);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG);
            val vorbereitungFromRepo = urnenwahlVorbereitungRepository.findById(wahlbezirkID).get();

            val expectedVorbereitung = urnenwahlvorbereitungModelMapper.toEntity(urnenwahlvorbereitungDTOMapper.toModel(wahlbezirkID, requestBody));

            Assertions.assertThat(vorbereitungFromRepo).usingRecursiveComparison().isEqualTo(expectedVorbereitung);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_URNENWAHLVORBEREITUNG })
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val requestBody = UrnenwahlVorbereitungTestdatenfactory.initValidDTO("wahlID").build();
            val request = buildPostRequest(wahlbezirkID, requestBody);

            val firstNewWahlurne = requestBody.urnenAnzahl().get(0);
            val oldUrnen = List.of(new Wahlurne(firstNewWahlurne.wahlID() + "Old", firstNewWahlurne.anzahl() + 2, firstNewWahlurne.urneVersiegelt()));
            val entityToWriteOver = new UrnenwahlVorbereitung(wahlbezirkID, oldUrnen, requestBody.anzahlWahlkabinen(), requestBody.anzahlWahltische(),
                    requestBody.anzahlNebenraeume());
            urnenwahlVorbereitungRepository.save(entityToWriteOver);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG);
            val vorbereitungFromRepo = urnenwahlVorbereitungRepository.findById(wahlbezirkID).get();

            val expectedVorbereitung = urnenwahlvorbereitungModelMapper.toEntity(urnenwahlvorbereitungDTOMapper.toModel(wahlbezirkID, requestBody));

            Assertions.assertThat(vorbereitungFromRepo).usingRecursiveComparison().isEqualTo(expectedVorbereitung);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_URNENWAHLVORBEREITUNG })
        void gotWlsExceptionWhenDataIsInvalid() throws Exception {
            val wahlbezirkID = "wahlbezirkID";

            val requestBody = UrnenwahlVorbereitungTestdatenfactory.initValidDTO("wahlID").urnenAnzahl(Collections.emptyList()).build();
            val request = buildPostRequest(wahlbezirkID, requestBody);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromRepsonse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG);
            Assertions.assertThat(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromRepsonse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_POST_URNENWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_URNENWAHLVORBEREITUNG })
        void gotWlsExceptionWhenNotSaveableCauseOfToLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";

            val requestBody = UrnenwahlVorbereitungTestdatenfactory.initValidDTO("wahlID").build();
            val request = buildPostRequest(wahlbezirkID, requestBody);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromRepsonse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLBORBEREITUNG);
            Assertions.assertThat(urnenwahlVorbereitungRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromRepsonse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final UrnenwahlvorbereitungWriteDTO requestBody) throws Exception {
            return post("/businessActions/urnenwahlVorbereitung/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }
}
