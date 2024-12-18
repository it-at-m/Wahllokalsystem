package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.wahlscheine;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static de.muenchen.oss.wahllokalsystem.wls.common.security.Profiles.NO_BEZIRKS_ID_CHECK;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.Wahlscheine;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.wahlscheine.WahlscheineRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine.WahlscheineModelMapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
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
public class WahlscheineControllerIntegrationTest {

    @Autowired
    WahlscheineRepository wahlscheineRepository;

    @Autowired
    WahlscheineModelMapper wahlscheineModelMapper;

    @Autowired
    WahlscheineDTOMapper wahlscheineDTOMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        wahlscheineRepository.deleteAll();
    }

    @Nested
    class GetWahlscheine {

        @Test
        void should_returnData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stimmabgabevermerke = 33;
            val request = MockMvcRequestBuilders.get(buildWahlscheineURI(wahlID, wahlbezirkID));

            val entityToFind = new Wahlscheine(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);
            wahlscheineRepository.save(entityToFind);

            val response = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();
            val responseBodyAsDTO = objectMapper.readValue(response.getContentAsString(), WahlscheineDTO.class);

            val expectedResult = wahlscheineDTOMapper.toDTO(wahlscheineModelMapper.toModel(entityToFind));

            Assertions.assertThat(responseBodyAsDTO)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedResult);
        }

        @Test
        void should_returnNoContent_when_dataIsNotPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildWahlscheineURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isNoContent()).andReturn().getResponse();

            Assertions.assertThat(response.getContentAsString()).isEmpty();
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val request = MockMvcRequestBuilders.get(buildWahlscheineURI(wahlID, wahlbezirkID));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.GET_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostWahlscheine {

        @Test
        void should_persistData_when_noDataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stimmabgabevermerke = 33L;
            val requestBody = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

            val request = MockMvcRequestBuilders.post(buildWahlscheineURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = wahlscheineRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = wahlscheineModelMapper.toEntity(wahlscheineDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedEntity);
        }

        @Test
        void should_replaceOldData_when_dataIsPresentInRepository() throws Exception {
            val wahlID = "wahlID";
            val wahlbezirkID = "wahlbezirkID";
            val stimmabgabevermerke = 33L;
            val requestBody = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

            val request = MockMvcRequestBuilders.post(buildWahlscheineURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val entityToReplace = new Wahlscheine(requestBody.bezirkUndWahlID(), 77L);
            Assertions.assertThat(entityToReplace).usingRecursiveComparison().isNotEqualTo(requestBody);
            wahlscheineRepository.save(entityToReplace);

            WireMock.stubFor(WireMock.post(UrlPattern.ANY).willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())));

            mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse();

            val entityFromRepo = wahlscheineRepository.findById(requestBody.bezirkUndWahlID()).get();
            val expectedEntity = wahlscheineModelMapper.toEntity(wahlscheineDTOMapper.toModel(requestBody));
            Assertions.assertThat(entityFromRepo)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedEntity);
        }

        @Test
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val wahlID = "    ";
            val wahlbezirkID = "wahlbezirkID";
            val stimmabgabevermerke = 33L;
            val requestBody = new WahlscheineDTO(new BezirkUndWahlID(wahlID, wahlbezirkID), stimmabgabevermerke);

            val request = MockMvcRequestBuilders.post(buildWahlscheineURI(wahlID, wahlbezirkID)).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestBody));

            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn().getResponse();
            val receivedWlsException = objectMapper.readValue(response.getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.code(),
                    "WLS-ERGEBNISMELDUNG", ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG.message());
            Assertions.assertThat(receivedWlsException).isEqualTo(expectedWlsExceptionDTO);
        }
    }

    private String buildWahlscheineURI(final String wahlID, final String wahlbezirkID) {
        return "/businessActions/wahlscheine/" + wahlID + "/" + wahlbezirkID;
    }
}
