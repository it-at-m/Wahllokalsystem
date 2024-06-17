package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.urnenwahlschliessungsuhrzeit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UrnenwahlSchliessungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.urnenwahlschliessungsuhrzeit.UrnenwahlSchliessungsUhrzeitModelMapper;
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
import org.springframework.beans.factory.annotation.Value;
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
public class UrnenwahlSchliessungsUhrzeitControllerIntegrationTest {

    @Value("${service.info.oid}")
    private String service_info_oid;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UrnenwahlSchliessungsUhrzeitModelMapper urnenwahlSchliessungsUhrzeitModelMapper;

    @Autowired
    UrnenwahlSchliessungsUhrzeitDTOMapper urnenwahlSchliessungsUhrzeitDTOMapper;

    @Autowired
    UrnenwahlSchliessungsUhrzeitRepository urnenwahlSchliessungsUhrzeitRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_URNENWAHLSCHLIESSUNGSUHRZEIT);
        urnenwahlSchliessungsUhrzeitRepository.deleteAll();
    }

    @Nested
    class GetUrnenwahlSchliessungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "123";
            val urnenwahlSchliessungsUhrzeitToFind = new UrnenwahlSchliessungsUhrzeit();
            urnenwahlSchliessungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDToFind);
            urnenwahlSchliessungsUhrzeitToFind.setUrnenwahlSchliessungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            urnenwahlSchliessungsUhrzeitRepository.save(urnenwahlSchliessungsUhrzeitToFind);
            val expectedResponseBody = urnenwahlSchliessungsUhrzeitDTOMapper.toDTO(
                    urnenwahlSchliessungsUhrzeitModelMapper.toModel(urnenwahlSchliessungsUhrzeitToFind));

            val request = get("/businessActions/urnenwahlSchliessungsUhrzeit/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UrnenwahlSchliessungsUhrzeitDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void noDataFound() throws Exception {
            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val urnenwahlSchliessungsUhrzeitToFind = new UrnenwahlSchliessungsUhrzeit();
            urnenwahlSchliessungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            urnenwahlSchliessungsUhrzeitToFind.setUrnenwahlSchliessungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            urnenwahlSchliessungsUhrzeitRepository.save(urnenwahlSchliessungsUhrzeitToFind);

            val request = get("/businessActions/urnenwahlSchliessungsUhrzeit/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

    @Nested
    class PostUrnenwahlSchliessungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new UrnenwahlSchliessungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request = buildPostRequest(wahlbezirkID, writeDto);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT);
            val urnenwahlSchliessungsUhrzeitFromRepo = urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUrnenwahlSchliessungsUhrzeit = urnenwahlSchliessungsUhrzeitModelMapper.toEntity(
                    urnenwahlSchliessungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto));

            Assertions.assertThat(urnenwahlSchliessungsUhrzeitFromRepo).isEqualTo(expectedUrnenwahlSchliessungsUhrzeit);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto1 = new UrnenwahlSchliessungsUhrzeitWriteDTO(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
            val request1 = buildPostRequest(wahlbezirkID, writeDto1);

            mockMvc.perform(request1).andExpect(status().isCreated());
            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT);
            val urnenwahlSchliessungsUhrzeitFromRepo1 = urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUrnenwahlSchliessungsUhrzeit1 = urnenwahlSchliessungsUhrzeitModelMapper.toEntity(
                    urnenwahlSchliessungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto1));

            Assertions.assertThat(urnenwahlSchliessungsUhrzeitFromRepo1).isEqualTo(expectedUrnenwahlSchliessungsUhrzeit1);

            val writeDto2 = new UrnenwahlSchliessungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request2 = buildPostRequest(wahlbezirkID, writeDto2);

            mockMvc.perform(request2).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT);
            val urnenwahlSchliessungsUhrzeitFromRepo2 = urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUrnenwahlSchliessungsUhrzeit2 = urnenwahlSchliessungsUhrzeitModelMapper.toEntity(
                    urnenwahlSchliessungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto2));

            Assertions.assertThat(urnenwahlSchliessungsUhrzeitFromRepo2).isEqualTo(expectedUrnenwahlSchliessungsUhrzeit2);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenParameterNotComplete() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new UrnenwahlSchliessungsUhrzeitWriteDTO(null);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT);
            Assertions.assertThat(urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), service_info_oid,
                    ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_URNENWAHLSCHLIESSUNGSUHRZEIT,
                        Authorities.REPOSITORY_WRITE_URNENWAHLSCHLIESSUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenNotSaveableCauseOfTooLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";
            val writeDto = new UrnenwahlSchliessungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_URNENWAHLSCHLIESSUNGSUHRZEIT);
            Assertions.assertThat(urnenwahlSchliessungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);

        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final UrnenwahlSchliessungsUhrzeitWriteDTO requestBody) throws Exception {
            return post("/businessActions/urnenwahlSchliessungsUhrzeit/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }

}
