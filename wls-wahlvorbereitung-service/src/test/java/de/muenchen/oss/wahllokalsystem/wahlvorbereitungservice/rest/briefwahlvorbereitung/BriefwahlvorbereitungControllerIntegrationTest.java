package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.briefwahlvorbereitung;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Briefwahlvorbereitung;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.BriefwahlvorbereitungRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.domain.Wahlurne;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.common.WahlurneDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.briefwahlvorbereitung.BriefwahlvorbereitungModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.SecurityUtils;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.utils.testdaten.WahlurneTestdatenfactory;
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
public class BriefwahlvorbereitungControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    BriefwahlvorbereitungModelMapper briefwahlvorbereitungModelMapper;

    @Autowired
    BriefwahlvorbereitungDTOMapper briefwahlvorbereitungDTOMapper;

    @Autowired
    BriefwahlvorbereitungRepository briefwahlvorbereitungRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runAs(Authorities.REPOSITORY_DELETE_BRIEFWAHLVORBEREITUNG);
        briefwahlvorbereitungRepository.deleteAll();
    }

    @Nested
    class GetBriefwahlvorbereitung {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG,
                        Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG }
        )
        void dataFound() throws Exception {
            val wahlbezirkIDToFind = "123";
            val briefwahlvorbereitungToFind = new Briefwahlvorbereitung();
            List<Wahlurne> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValid("1234").build());
            briefwahlvorbereitungToFind.setWahlbezirkID(wahlbezirkIDToFind);
            briefwahlvorbereitungToFind.setUrnenAnzahl(urnenanzahl1);
            briefwahlvorbereitungRepository.save(briefwahlvorbereitungToFind);
            val expectedResponseBody = briefwahlvorbereitungDTOMapper.toDTO(briefwahlvorbereitungModelMapper.toModel(briefwahlvorbereitungToFind));

            val request = get("/businessActions/briefwahlvorbereitung/" + wahlbezirkIDToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), BriefwahlvorbereitungDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_GET_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG,
                        Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG }
        )
        void noDataFound() throws Exception {
            val wahlbezirkIDEmpty = "123";

            val wahlbezirkIDNotEmpty = "456";
            val briefwahlvorbereitungToFind = new Briefwahlvorbereitung();
            List<Wahlurne> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValid("1234").build());
            briefwahlvorbereitungToFind.setWahlbezirkID(wahlbezirkIDNotEmpty);
            briefwahlvorbereitungToFind.setUrnenAnzahl(urnenanzahl1);
            briefwahlvorbereitungRepository.save(briefwahlvorbereitungToFind);

            val request = get("/businessActions/briefwahlvorbereitung/" + wahlbezirkIDEmpty);

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }
    }

    @Nested
    class PostBriefwahlvorbereitung {
        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG }
        )
        void newDataIsSaved() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            List<WahlurneDTO> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidDTO("1234").build());
            val writeDto = new BriefwahlvorbereitungWriteDTO(urnenanzahl1);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            mockMvc.perform(request).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG);
            val briefwahlvorbereitungFromRepo = briefwahlvorbereitungRepository.findById(wahlbezirkID).get();
            val expectedBriefwahlvorbereitung = briefwahlvorbereitungModelMapper.toEntity(briefwahlvorbereitungDTOMapper.toModel(wahlbezirkID, writeDto));

            Assertions.assertThat(briefwahlvorbereitungFromRepo).usingRecursiveComparison().isEqualTo(expectedBriefwahlvorbereitung);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG }
        )
        void existingDataIsOverwritten() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            List<WahlurneDTO> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidDTO("1234").build());
            val writeDto1 = new BriefwahlvorbereitungWriteDTO(urnenanzahl1);
            val request1 = buildPostRequest(wahlbezirkID, writeDto1);

            mockMvc.perform(request1).andExpect(status().isCreated());
            SecurityUtils.runAs(Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG);
            val briefwahlvorbereitungFromRepo1 = briefwahlvorbereitungRepository.findById(wahlbezirkID).get();
            val expectedBriefwahlvorbereitung1 = briefwahlvorbereitungModelMapper.toEntity(briefwahlvorbereitungDTOMapper.toModel(wahlbezirkID, writeDto1));

            Assertions.assertThat(briefwahlvorbereitungFromRepo1).usingRecursiveComparison().isEqualTo(expectedBriefwahlvorbereitung1);

            val writeDto2 = new BriefwahlvorbereitungWriteDTO(Collections.emptyList());
            val request2 = buildPostRequest(wahlbezirkID, writeDto2);

            mockMvc.perform(request2).andExpect(status().isCreated());

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG);
            val briefwahlvorbereitungFromRepo2 = briefwahlvorbereitungRepository.findById(wahlbezirkID).get();
            val expectedBriefwahlvorbereitung2 = briefwahlvorbereitungModelMapper.toEntity(briefwahlvorbereitungDTOMapper.toModel(wahlbezirkID, writeDto2));

            Assertions.assertThat(briefwahlvorbereitungFromRepo2).usingRecursiveComparison().isEqualTo(expectedBriefwahlvorbereitung2);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG }
        )
        void gotWlsExceptionWhenParameterNotComplete() throws Exception {
            val wahlbezirkID = "wahlbezirkID";
            val writeDto = new BriefwahlvorbereitungWriteDTO(null);
            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG);
            Assertions.assertThat(briefwahlvorbereitungRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.PARAMS_UNVOLLSTAENDIG.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.PARAMS_UNVOLLSTAENDIG.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.SERVICE_POST_BRIEFWAHLVORBEREITUNG, Authorities.REPOSITORY_WRITE_BRIEFWAHLVORBEREITUNG }
        )
        void gotWlsExceptionWhenNotSaveableCauseOfTooLongData() throws Exception {
            val wahlbezirkID = StringUtils.leftPad(" ", 255) + "wahlbezirkID";
            List<WahlurneDTO> urnenanzahl1 = List.of(WahlurneTestdatenfactory.initValidDTO("1234").build());
            val writeDto = new BriefwahlvorbereitungWriteDTO(urnenanzahl1);

            val request = buildPostRequest(wahlbezirkID, writeDto);

            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val exceptionBodyFromResponse = objectMapper.readValue(response.getResponse().getContentAsString(StandardCharsets.UTF_8), WlsExceptionDTO.class);

            SecurityUtils.runAs(Authorities.REPOSITORY_READ_BRIEFWAHLVORBEREITUNG);
            Assertions.assertThat(briefwahlvorbereitungRepository.findById(wahlbezirkID)).isEmpty();

            val expectedExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.UNSAVEABLE.code(), "WLS-WAHLVORBEREITUNG",
                    ExceptionConstants.UNSAVEABLE.message());
            Assertions.assertThat(exceptionBodyFromResponse).usingRecursiveComparison().isEqualTo(expectedExceptionDTO);
        }

        private RequestBuilder buildPostRequest(final String wahlbezirkID, final BriefwahlvorbereitungWriteDTO requestBody) throws Exception {
            return post("/businessActions/briefwahlvorbereitung/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }

}
