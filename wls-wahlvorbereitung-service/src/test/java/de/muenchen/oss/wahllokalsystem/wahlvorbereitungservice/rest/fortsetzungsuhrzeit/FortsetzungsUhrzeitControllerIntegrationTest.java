package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.fortsetzungsuhrzeit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.FortsetzungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.FortsetzungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.fortsetzungsuhrzeit.FortsetzungsUhrzeitModelMapper;
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
public class FortsetzungsUhrzeitControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FortsetzungsUhrzeitModelMapper fortsetzungsUhrzeitModelMapper;

    @Autowired
    FortsetzungsUhrzeitDTOMapper fortsetzungsUhrzeitDTOMapper;

    @Autowired
    FortsetzungsUhrzeitRepository fortsetzungsUhrzeitRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_FORTSETZUNGSUHRZEIT);
        fortsetzungsUhrzeitRepository.deleteAll();
    }

    @Nested
    class GetFortsetzungsUhrzeit {
        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT,
                Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT }
        )
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "123";
            val fortsetzungsUhrzeitToFind = new FortsetzungsUhrzeit();
            fortsetzungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDToFind);
            fortsetzungsUhrzeitToFind.setFortsetzungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            fortsetzungsUhrzeitRepository.save(fortsetzungsUhrzeitToFind);
            val expectedResponseBody = fortsetzungsUhrzeitDTOMapper.toDTO(fortsetzungsUhrzeitModelMapper.toModel(fortsetzungsUhrzeitToFind));

            val request = get("/businessActions/fortsetzungsUhrzeit/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), FortsetzungsUhrzeitDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT,
                Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT }
        )
        void noDataFound() throws Exception {
            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val fortsetzungsUhrzeitToFind = new FortsetzungsUhrzeit();
            fortsetzungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            fortsetzungsUhrzeitToFind.setFortsetzungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            fortsetzungsUhrzeitRepository.save(fortsetzungsUhrzeitToFind);

            val request = get("/businessActions/fortsetzungsUhrzeit/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

    @Nested
    class PostFortsetzungsUhrzeit {
        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT }
        )
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new FortsetzungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request = buildPostRequest(wahlbezirkID, writeDto);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT);
            val fortsetzungsUhrzeitFromRepo = fortsetzungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedFortsetzungsUhrzeit = fortsetzungsUhrzeitModelMapper.toEntity(fortsetzungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto));

            Assertions.assertThat(fortsetzungsUhrzeitFromRepo).isEqualTo(expectedFortsetzungsUhrzeit);
        }

        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT }
        )
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto1 = new FortsetzungsUhrzeitWriteDTO(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
            val request1 = buildPostRequest(wahlbezirkID, writeDto1);

            mockMvc.perform(request1).andExpect(status().isCreated());
            SecurityUtils.runAs(Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT);
            val fortsetzungsUhrzeitFromRepo1 = fortsetzungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedFortsetzungsUhrzeit1 = fortsetzungsUhrzeitModelMapper.toEntity(fortsetzungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto1));

            Assertions.assertThat(fortsetzungsUhrzeitFromRepo1).isEqualTo(expectedFortsetzungsUhrzeit1);

            val writeDto2 = new FortsetzungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request2 = buildPostRequest(wahlbezirkID, writeDto2);

            mockMvc.perform(request2).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT);
            val fortsetzungsUhrzeitFromRepo2 = fortsetzungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedFortsetzungsUhrzeit2 = fortsetzungsUhrzeitModelMapper.toEntity(fortsetzungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto2));

            Assertions.assertThat(fortsetzungsUhrzeitFromRepo2).isEqualTo(expectedFortsetzungsUhrzeit2);
        }

        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenParameterNotComplete() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new FortsetzungsUhrzeitWriteDTO(null);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT);
            Assertions.assertThat(fortsetzungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), "WLS-WAHLVORBEREITUNG",
                ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(
            authorities = { Authorities.SERVICE_FORTSETZUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_FORTSETZUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenNotSaveableCauseOfTooLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";
            val writeDto = new FortsetzungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_FORTSETZUNGSUHRZEIT);
            Assertions.assertThat(fortsetzungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);

        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final FortsetzungsUhrzeitWriteDTO requestBody) throws Exception {
            return post("/businessActions/fortsetzungsUhrzeit/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                objectMapper.writeValueAsString(requestBody));
        }
    }

}
