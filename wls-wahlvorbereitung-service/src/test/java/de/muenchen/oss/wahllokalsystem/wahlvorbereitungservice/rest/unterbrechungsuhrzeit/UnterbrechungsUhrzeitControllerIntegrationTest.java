package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeit;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.UnterbrechungsUhrzeitRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit.UnterbrechungsUhrzeitModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
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
public class UnterbrechungsUhrzeitControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UnterbrechungsUhrzeitModelMapper unterbrechungsUhrzeitModelMapper;

    @Autowired
    UnterbrechungsUhrzeitDTOMapper unterbrechungsUhrzeitDTOMapper;

    @Autowired
    UnterbrechungsUhrzeitRepository unterbrechungsUhrzeitRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_UNTERBRECHUNGSUHRZEIT);
        unterbrechungsUhrzeitRepository.deleteAll();
    }

    @Nested
    class GetUnterbrechungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT }
        )
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "123";
            val unterbrechungsUhrzeitToFind = new UnterbrechungsUhrzeit();
            unterbrechungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDToFind);
            unterbrechungsUhrzeitToFind.setUnterbrechungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            unterbrechungsUhrzeitRepository.save(unterbrechungsUhrzeitToFind);
            val expectedResponseBody = unterbrechungsUhrzeitDTOMapper.toDTO(unterbrechungsUhrzeitModelMapper.toModel(unterbrechungsUhrzeitToFind));

            val request = get("/businessActions/unterbrechungsUhrzeit/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), UnterbrechungsUhrzeitDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT,
                        Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT }
        )
        void noDataFound() throws Exception {
            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val unterbrechungsUhrzeitToFind = new UnterbrechungsUhrzeit();
            unterbrechungsUhrzeitToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            unterbrechungsUhrzeitToFind.setUnterbrechungsUhrzeit(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            unterbrechungsUhrzeitRepository.save(unterbrechungsUhrzeitToFind);

            val request = get("/businessActions/unterbrechungsUhrzeit/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

    @Nested
    class PostUnterbrechungsUhrzeit {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT }
        )
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new UnterbrechungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request = buildPostRequest(wahlbezirkID, writeDto);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT);
            val unterbrechungsUhrzeitFromRepo = unterbrechungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUnterbrechungsUhrzeit = unterbrechungsUhrzeitModelMapper.toEntity(unterbrechungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto));

            Assertions.assertThat(unterbrechungsUhrzeitFromRepo).isEqualTo(expectedUnterbrechungsUhrzeit);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT }
        )
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto1 = new UnterbrechungsUhrzeitWriteDTO(LocalDateTime.of(2023, 1, 1, 12, 0, 0));
            val request1 = buildPostRequest(wahlbezirkID, writeDto1);

            mockMvc.perform(request1).andExpect(status().isCreated());
            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT);
            val unterbrechungsUhrzeitFromRepo1 = unterbrechungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUnterbrechungsUhrzeit1 = unterbrechungsUhrzeitModelMapper.toEntity(unterbrechungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto1));

            Assertions.assertThat(unterbrechungsUhrzeitFromRepo1).isEqualTo(expectedUnterbrechungsUhrzeit1);

            val writeDto2 = new UnterbrechungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
            val request2 = buildPostRequest(wahlbezirkID, writeDto2);

            mockMvc.perform(request2).andExpect(status().isCreated());

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT);
            val unterbrechungsUhrzeitFromRepo2 = unterbrechungsUhrzeitRepository.findById(wahlbezirkID).get();
            val expectedUnterbrechungsUhrzeit2 = unterbrechungsUhrzeitModelMapper.toEntity(unterbrechungsUhrzeitDTOMapper.toModel(wahlbezirkID, writeDto2));

            Assertions.assertThat(unterbrechungsUhrzeitFromRepo2).isEqualTo(expectedUnterbrechungsUhrzeit2);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenParameterNotComplete() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new UnterbrechungsUhrzeitWriteDTO(null);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT);
            Assertions.assertThat(unterbrechungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_UNTERBRECHUNGSUHRZEIT, Authorities.REPOSITORY_WRITE_UNTERBRECHUNGSUHRZEIT }
        )
        void gotWlsExceptionWhenNotSaveableCauseOfTooLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";
            val writeDto = new UnterbrechungsUhrzeitWriteDTO(LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromRepsonse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runWith(Authorities.REPOSITORY_READ_UNTERBRECHUNGSUHRZEIT);
            Assertions.assertThat(unterbrechungsUhrzeitRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromRepsonse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);

        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final UnterbrechungsUhrzeitWriteDTO requestBody) throws Exception {
            return post("/businessActions/unterbrechungsUhrzeit/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }

}
