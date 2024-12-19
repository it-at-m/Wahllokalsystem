package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelumschlaege;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.Stimmzettelumschlaege;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelumschlaege.StimmzettelumschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelumschlaege.StimmzettelumschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(
        profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE, NO_BEZIRKS_ID_CHECK }
)
public class StimmzettelumschlaegeControllerIntegrationTest {

    @Autowired
    StimmzettelumschlaegeRepository stimmzettelumschlaegeRepository;

    @Autowired
    StimmzettelumschlaegeModelMapper stimmzettelumschlaegeModelMapper;

    @Autowired
    StimmzettelumschlaegeDTOMapper stimmzettelumschlaegeDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        stimmzettelumschlaegeRepository.deleteAll();
    }

    @Nested
    class GetStimmzettelumschlaege {

        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val entityToFind = new Stimmzettelumschlaege(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);
            stimmzettelumschlaegeRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsDTO = objectMapper.readValue(response.getContentAsString(), StimmzettelumschlaegeDTO.class);

            val expectedResult = stimmzettelumschlaegeDTOMapper.toDTO(stimmzettelumschlaegeModelMapper.toModel(entityToFind));

            Assertions.assertThat(responseBodyAsDTO)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn().getResponse();

            Assertions.assertThat(response.getContentAsString()).isEmpty();
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.GET_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostStimmzettelumschlaege {

        @Test
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedEntity);
        }

        @Test
        void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val anzahlWaehlerToReplace = 8;
            val anzahlWaehler2ToReplace = 15L;
            val entityToReplace = new Stimmzettelumschlaege(requestBody.bezirkUndWahlID(), requestBody.urneneroeffnungsUhrzeit(), anzahlWaehlerToReplace, anzahlWaehler2ToReplace);
            Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
            stimmzettelumschlaegeRepository.save(entityToReplace);

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = stimmzettelumschlaegeRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = stimmzettelumschlaegeModelMapper.toEntity(stimmzettelumschlaegeDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedEntity);
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val urneneroeffnungsUhrzeit = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
            val anzahlWaehler = 47;
            val anzahlWaehler2 = 11L;
            val requestBody = new StimmzettelumschlaegeDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), urneneroeffnungsUhrzeit, anzahlWaehler, anzahlWaehler2);

            val request = MockMvcRequestBuilders.post(buildStimmzettelumschlaegeURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_STIMMZETTELUMSCHLAEGE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    private String buildStimmzettelumschlaegeURI(final String wahlID, final String wahlbezirkID) {
        return "/businessActions/stimmzettelumschlaege/" + wahlID + "/" + wahlbezirkID;
    }
}
