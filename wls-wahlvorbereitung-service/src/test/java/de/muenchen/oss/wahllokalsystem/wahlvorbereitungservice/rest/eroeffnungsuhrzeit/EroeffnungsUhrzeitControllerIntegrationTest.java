package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.eroeffnungsuhrzeit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.EroeffnungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.eroeffnungsuhrzeit.EroeffnungsUhrzeitModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
public class EroeffnungsUhrzeitControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EroeffnungsUhrzeitModelMapper eroeffnungsUhrzeitModelMapper;

    @Autowired
    EroeffnungsUhrzeitDTOMapper eroeffnungsUhrzeitDTOMapper;

    @Autowired
    EroeffnungsUhrzeitRepository eroeffnungsUhrzeitRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_EROEFFNUNGSUHRZEIT);
        eroeffnungsUhrzeitRepository.deleteAll();
    }

    @Nested
    class GetEroeffnungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT }
        )
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "123";
            val eroeffnungsUhrzeitToFind = new EroeffnungsUhrzeit();
            eroeffnungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDToFind);
            eroeffnungsUhrzeitToFind.setEroeffnungsuhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            eroeffnungsUhrzeitRepository.save(eroeffnungsUhrzeitToFind);
            val expectedResponseBody = eroeffnungsUhrzeitDTOMapper.toDTO(eroeffnungsUhrzeitModelMapper.toModel(eroeffnungsUhrzeitToFind));

            val request = get("/businessActions/eroeffnungsuhrzeit/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), EroeffnungsUhrzeitDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT }
        )
        void noDataFound() throws Exception {
            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val eroeffnungsUhrzeitToFind = new EroeffnungsUhrzeit();
            eroeffnungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            eroeffnungsUhrzeitToFind.setEroeffnungsuhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            eroeffnungsUhrzeitRepository.save(eroeffnungsUhrzeitToFind);

            val request = get("/businessActions/eroeffnungsuhrzeit/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

    @Nested
    class PostEroeffnungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT }
        )
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new EroeffnungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request = buildPostRequest(wahlbezirkID, writeDto);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT);
            val eroeffnungsUhrzeitFromRepo = eroeffnungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedEroeffnungsUhrzeit = eroeffnungsUhrzeitModelMapper.toEntity(eroeffnungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto));

            Assertions.assertThat(eroeffnungsUhrzeitFromRepo).isEqualTo(expectedEroeffnungsUhrzeit);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT }
        )
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto1 = new EroeffnungsUhrzeitWriteDTO(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
            val request1 = buildPostRequest(wahlbezirkID, writeDto1);

            mockMvc.perform(request1).andExpect(status().isCreated());
            SecurityUtils.runAs(Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT);
            val eroeffnungsUhrzeitFromRepo1 = eroeffnungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedEroeffnungsUhrzeit1 = eroeffnungsUhrzeitModelMapper.toEntity(eroeffnungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto1));

            Assertions.assertThat(eroeffnungsUhrzeitFromRepo1).isEqualTo(expectedEroeffnungsUhrzeit1);

            val writeDto2 = new EroeffnungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request2 = buildPostRequest(wahlbezirkID, writeDto2);

            mockMvc.perform(request2).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT);
            val eroeffnungsUhrzeitFromRepo2 = eroeffnungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedEroeffnungsUhrzeit2 = eroeffnungsUhrzeitModelMapper.toEntity(eroeffnungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto2));

            Assertions.assertThat(eroeffnungsUhrzeitFromRepo2).isEqualTo(expectedEroeffnungsUhrzeit2);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenParameterNotComplete() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new EroeffnungsUhrzeitWriteDTO(null);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT);
            Assertions.assertThat(eroeffnungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_EROEFFNUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_EROEFFNUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenNotSaveableCauseOfTooLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";
            val writeDto = new EroeffnungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromRepsonse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_EROEFFNUNGSUHRZEIT);
            Assertions.assertThat(eroeffnungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromRepsonse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);

        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final EroeffnungsUhrzeitWriteDTO requestBody) throws Exception {
            return post("/businessActions/eroeffnungsuhrzeit/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }

}
