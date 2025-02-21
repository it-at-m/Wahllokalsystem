package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahltermindaten;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class WahltermindatenControllerIntegrationTest {

    @Autowired
    MockMvc api;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

    }

    @AfterEach
    void teardown() {
        reset();
    }

    @Nested
    class LoadWahltermindaten {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_LOADWAHLTERMINDATEN })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val invalidWahltagID = " ";
            val request = post("/businessActions/importWahltermindaten/" + invalidWahltagID).with(csrf());

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.MISSING_ARGUMENT.code(),
                    "WLS-ADMIN", ExceptionConstants.MISSING_ARGUMENT.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_LOADWAHLTERMINDATEN })
        void should_returnOK_when_allRemoteClientsAreCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/importWahltermindaten/" + wahltagID).with(csrf());

            stubFor(WireMock.put("/businessActions/wahltermindaten/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            val wahltageList = List.of(new WahltagModel("wahltagID", LocalDate.now().minusMonths(2), "Beschreibung Wahltag 1", "0"));
            stubFor(WireMock.get("/businessActions/wahltage").willReturn(createWireMockResponse(wahltageList, HttpStatus.OK)));

            val wahlbezirkeList = List.of(new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", LocalDate.now(), "0", "wahlID1"));
            stubFor(WireMock.get("/businessActions/wahlbezirke/" + wahltagID).willReturn(createWireMockResponse(wahlbezirkeList, HttpStatus.OK)));

            stubFor(WireMock.post("/businessActions/awerte/init").willReturn(createWireMockResponse(HttpStatus.OK)));

            stubFor(WireMock.post("/businessActions/konfigurierterWahltag").willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk()).andReturn();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_LOADWAHLTERMINDATEN })
        void should_returnInternalServerErrorAndRollback_when_konfigurierterWahltagCouldNotBeSet() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/importWahltermindaten/" + wahltagID).with(csrf());

            stubFor(WireMock.put("/businessActions/wahltermindaten/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            val wahltageList = List.of(new WahltagModel("wahltagID", LocalDate.now().minusMonths(2), "Beschreibung Wahltag 1", "0"));
            stubFor(WireMock.get("/businessActions/wahltage").willReturn(createWireMockResponse(wahltageList, HttpStatus.OK)));

            val wahlbezirkeList = List.of(new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", LocalDate.now(), "0", "wahlID1"));
            stubFor(WireMock.get("/businessActions/wahlbezirke/" + wahltagID).willReturn(createWireMockResponse(wahlbezirkeList, HttpStatus.OK)));

            stubFor(WireMock.post("/businessActions/awerte/init").willReturn(createWireMockResponse(HttpStatus.OK)));

            // missing stubbing for "/businessActions/konfigurierterWahltag" throws exception and service tries to delete
            stubFor(WireMock.delete("/businessActions/wahltermindaten/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isInternalServerError()).andReturn();
        }

    }

    @Nested
    class DeleteWahltermindaten {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLTERMINDATEN })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val invalidWahltagID = " ";
            val request = post("/businessActions/deleteWahltermindaten/" + invalidWahltagID).with(csrf());

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.MISSING_ARGUMENT.code(),
                    "WLS-ADMIN", ExceptionConstants.MISSING_ARGUMENT.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLTERMINDATEN })
        void should_returnOK_when_allRemoteClientsAreCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/deleteWahltermindaten/" + wahltagID).with(csrf());

            stubFor(WireMock.delete("/businessActions/wahltermindaten/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            stubFor(WireMock.delete("/businessActions/konfigurierterWahltag/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk()).andReturn();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLTERMINDATEN })
        void should_returnInternalServer_when_wahltermindatenCouldNotBeDeleted() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/deleteWahltermindaten/" + wahltagID).with(csrf());

            // missing stubbing for "/businessActions/wahltermindaten" throws exception

            stubFor(WireMock.delete("/businessActions/konfigurierterWahltag/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isInternalServerError()).andReturn();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLTERMINDATEN })
        void should_returnInternalServer_when_konfigurierterWahltagCouldNotBeDeleted() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/deleteWahltermindaten/" + wahltagID).with(csrf());

            stubFor(WireMock.delete("/businessActions/wahltermindaten/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            // missing stubbing for "/businessActions/konfigurierterWahltag" throws exception

            api.perform(request).andExpect(status().isInternalServerError()).andReturn();
        }

    }

    private ResponseDefinitionBuilder createWireMockResponse(final HttpStatus responseStatus) {
        return aResponse()
                .withStatus(responseStatus.value());
    }

    private ResponseDefinitionBuilder createWireMockResponse(final Object responseBody, final HttpStatus responseStatus) throws Exception {
        return aResponse()
                .withBody(objectMapper.writeValueAsString(responseBody))
                .withHeader("Content-Type", "application/json")
                .withStatus(responseStatus.value());
    }
}
