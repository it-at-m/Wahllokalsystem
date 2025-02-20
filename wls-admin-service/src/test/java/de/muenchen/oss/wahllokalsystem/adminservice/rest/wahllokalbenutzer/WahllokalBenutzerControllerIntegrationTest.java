package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahllokalbenutzer;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
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
class WahllokalBenutzerControllerIntegrationTest {

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
    class GenerateWahllokalbenutzer {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_GENERATEEXPORTWAHLLOKALBENUTZER })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val invalidWahltagID = " ";
            val request = post("/businessActions/generateWahllokalbenutzer/" + invalidWahltagID).with(csrf());

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.MISSING_ARGUMENT.code(),
                    "WLS-ADMIN", ExceptionConstants.MISSING_ARGUMENT.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_GENERATEEXPORTWAHLLOKALBENUTZER })
        void should_returnOK_when_allRemoteClientsAreCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/generateWahllokalbenutzer/" + wahltagID).with(csrf());

            val wahlbezirkeList = List.of(new WahlbezirkModel("wahlbezirkID", WahlbezirkArtModel.UWB, "4711", LocalDate.now(), "0", "wahlID"));
            stubFor(WireMock.get("/businessActions/wahlbezirke/" + wahltagID).willReturn(createWireMockResponse(wahlbezirkeList, HttpStatus.OK)));
            stubFor(WireMock.post("/generateAndExportWahllokalbenutzer/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk()).andReturn();
        }
    }

    @Nested
    class ExportWahllokalBenutzer {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_EXPORTWAHLLOKALBENUTZER })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val invalidWahltagID = " ";
            val request = get("/businessActions/exportWahllokalBenutzer/" + invalidWahltagID).with(csrf());

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.MISSING_ARGUMENT.code(),
                    "WLS-ADMIN", ExceptionConstants.MISSING_ARGUMENT.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_EXPORTWAHLLOKALBENUTZER })
        void should_returnOK_when_remoteClientIsCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val request = get("/businessActions/exportWahllokalBenutzer/" + wahltagID).with(csrf());

            stubFor(WireMock.get("/exportWahllokalbenutzer/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk()).andReturn();
        }
    }

    @Nested
    class DeleteWahllokalBenutzer {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLLOKALBENUTZER })
        void should_returnBadRequestWlsException_when_validationFailed() throws Exception {
            val invalidWahltagID = " ";
            val request = post("/businessActions/deleteWahllokalBenutzer/" + invalidWahltagID).with(csrf());

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.MISSING_ARGUMENT.code(),
                    "WLS-ADMIN", ExceptionConstants.MISSING_ARGUMENT.message());

            val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
            val resultBodyAsWlsExceptionDTO = objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

            Assertions.assertThat(resultBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
            Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
        }

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_DELETEWAHLLOKALBENUTZER })
        void should_returnOK_when_remoteClientIsCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val request = post("/businessActions/deleteWahllokalBenutzer/" + wahltagID).with(csrf());

            stubFor(WireMock.delete("/deleteWahllokalbenutzer/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

            api.perform(request).andExpect(status().isOk()).andReturn();
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