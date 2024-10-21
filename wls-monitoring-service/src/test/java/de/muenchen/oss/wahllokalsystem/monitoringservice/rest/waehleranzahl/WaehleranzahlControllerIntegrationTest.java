package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.client.waehleranzahl.WaehleranzahlClientMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.Waehleranzahl;
import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.waehleranzahl.WaehleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl.WaehleranzahlModelMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDateTime;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
public class WaehleranzahlControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    ObjectMapper objectMapper;

    @SpyBean
    WaehleranzahlRepository wahleranzahlRepository;

    @Autowired
    WaehleranzahlDTOMapper waehleranzahlDTOMapper;

    @Autowired
    WaehleranzahlModelMapper waehleranzahlModelMapper;

    @Autowired
    WaehleranzahlClientMapper waehleranzahlClientMapper;

    @Autowired
    MockMvc api;

    @Autowired
    WaehleranzahlRepository waehleranzahlRepository;

    @AfterEach
    void tearDown() {
        SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAEHLERANZAHL);
        waehleranzahlRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class GetWahlbeteiligung {

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERANZAHL, Authorities.REPOSITORY_READ_WAEHLERANZAHL })
        void should_returnEmptyResponse_when_NoDataFound() throws Exception {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val request = MockMvcRequestBuilders.get("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID);

            val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

            Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERANZAHL, Authorities.REPOSITORY_READ_WAEHLERANZAHL })
        void should_returnOkAndData_when_DataFound() throws Exception {
            val wahlID = "wahlID01";
            val wahlbezirkID = "wahlbezirkID01";
            val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
            val anzahlWaehler = 99;
            val uhrzeit = LocalDateTime.parse("2024-09-13T12:11:21.343");

            val waehleranzahlToFind = new Waehleranzahl(bezirkUndWahlID, anzahlWaehler, uhrzeit);
            waehleranzahlRepository.save(waehleranzahlToFind);
            val expectedResponseBody = waehleranzahlDTOMapper.toDTO(waehleranzahlModelMapper.toModel(waehleranzahlToFind));

            val request = MockMvcRequestBuilders.get("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID);

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBodyAsDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WaehleranzahlDTO.class);

            Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
        }

        @Nested
        class PostBriefwahlvorbereitung {
            @Test
            @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERANZAHL, Authorities.REPOSITORY_READ_WAEHLERANZAHL })
            void should_overwriteExistingData_when_NewDataIsStoredWithSameID() throws Exception {
                val wahlID = "wahlID01";
                val wahlbezirkID = "wahlbezirkID01";
                val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
                val anzahlWaehler_1 = 99L;
                val uhrzeit_1 = LocalDateTime.parse("2024-09-13T12:11:21.343");

                val waehleranzahlDTO_1 = new WaehleranzahlDTO(wahlID, wahlbezirkID, anzahlWaehler_1, uhrzeit_1);

                WireMock.stubFor(WireMock.post("/wahlbeteiligung")
                        .willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json").withStatus(HttpStatus.OK.value())
                                .withBody(objectMapper.writeValueAsBytes(waehleranzahlDTO_1))));

                val request_1 = buildPostRequest(wahlID, wahlbezirkID, waehleranzahlDTO_1);
                api.perform(request_1).andExpect(status().isOk()).andReturn();

                SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERANZAHL);
                val waehleranzahlFromRepo_1 = waehleranzahlRepository.findById(bezirkUndWahlID).get();
                val expectedWaehleranzahl_1 = waehleranzahlModelMapper.toEntity(waehleranzahlDTOMapper.toSetModel(bezirkUndWahlID, waehleranzahlDTO_1));

                Assertions.assertThat(waehleranzahlFromRepo_1).usingRecursiveComparison().isEqualTo(expectedWaehleranzahl_1);

                // Overwrite existing data
                val anzahlWaehler_2 = 55L;
                val uhrzeit_2 = LocalDateTime.parse("2024-09-13T12:11:21.666");
                val waehleranzahlDTO_2 = new WaehleranzahlDTO(wahlID, wahlbezirkID, anzahlWaehler_2, uhrzeit_2);

                val request_2 = buildPostRequest(wahlID, wahlbezirkID, waehleranzahlDTO_2);
                api.perform(request_2).andExpect(status().isOk()).andReturn();

                SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERANZAHL);
                val waehleranzahlFromRepo_2 = waehleranzahlRepository.findById(bezirkUndWahlID).get();
                val expectedWaehleranzahl_2 = waehleranzahlModelMapper.toEntity(waehleranzahlDTOMapper.toSetModel(bezirkUndWahlID, waehleranzahlDTO_2));

                Assertions.assertThat(waehleranzahlFromRepo_2).usingRecursiveComparison().isEqualTo(expectedWaehleranzahl_2);
            }
        }

        @Test
        @WithMockUser(authorities = { Authorities.SERVICE_GET_WAEHLERANZAHL, Authorities.REPOSITORY_READ_WAEHLERANZAHL })
        void should_throwTechnischeWlsException_when_requestIsInvalid() throws Exception {
            val wahlID = "_   ";
            val wahlbezirkID = "wahlbezirkID01";
            val anzahlWaehler = 99L;
            val uhrzeit = LocalDateTime.parse("2024-09-13T12:11:21.343");
            val waehleranzahlDTO = new WaehleranzahlDTO(wahlID, wahlbezirkID, anzahlWaehler, uhrzeit);

            val request = MockMvcRequestBuilders.post("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID).with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                            objectMapper.writeValueAsString(waehleranzahlDTO));

            Mockito.doThrow(new RuntimeException("DB-Error")).when(wahleranzahlRepository).save(any());

            val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();

            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE.code(),
                    serviceID, ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
        }

        private RequestBuilder buildPostRequest(final String wahlID, final String wahlbezirkID, final WaehleranzahlDTO requestBody) throws Exception {
            return post("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(requestBody));
        }
    }
}
