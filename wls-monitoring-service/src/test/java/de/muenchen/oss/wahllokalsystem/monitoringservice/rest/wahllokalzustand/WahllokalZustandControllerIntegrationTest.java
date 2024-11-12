package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.wahllokalzustand;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionKonstanten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class WahllokalZustandControllerIntegrationTest {

    @Value("${service.info.oid}")
    String serviceID;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        WireMock.resetAllRequests();
    }

    @Nested
    class PostLastSeen {

        @Test
        void should_notThrowAnyException_when_requestParamValid() {
            val request = MockMvcRequestBuilders.post("/businessActions/lastSeen/" + "validWahlbezirkID").with(csrf());
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request));
        }

        @Test
        void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/lastSeen/" + "  ").with(csrf());
            val response = mockMvc.perform(request).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
                    serviceID, ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostLetzteAbmeldung {

        @Test
        void should_notThrowAnyException_when_requestParamValid() {
            val request = MockMvcRequestBuilders.post("/businessActions/letzteAbmeldung/" + "validWahlbezirkID").with(csrf());
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request));
        }

        @Test
        void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
            val request = MockMvcRequestBuilders.post("/businessActions/letzteAbmeldung/").with(csrf());
            val response = mockMvc.perform(request).andExpect(status().isInternalServerError()).andReturn();
            val responseBodyAsWlsExceptionDTO = objectMapper.readValue(response.getResponse().getContentAsString(), WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.T, ExceptionKonstanten.CODE_ALLGEMEIN_UNBEKANNT, "WLS-MONITORING", "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO).usingRecursiveComparison().ignoringFields("message").isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostSchnellmeldungsSendungsuhrzeit {

        @Test
        void should_notThrowAnyException_when_requestParamValid() throws Exception {
            val sendungsDatenDTO_valid = createSendungsdatenDTO("wahlID", "wahlbezirkID");
            val request_valid_param = MockMvcRequestBuilders.post("/businessActions/schnellmeldungSendungsuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(sendungsDatenDTO_valid));
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
        }

        @Test
        void should_throwWlsException_when_requestParamIsInvalid() throws Exception {
            val request_sendungsDatenNull = MockMvcRequestBuilders.post("/businessActions/schnellmeldungSendungsuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(null));
            val response_sendungsDatenNull = mockMvc.perform(request_sendungsDatenNull).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO_sendungsDatenNull = objectMapper.readValue(response_sendungsDatenNull.getResponse().getContentAsString(),
                    WlsExceptionDTO.class);

            val expectedWlsExceptionDTO_sendungsDatenNull = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE,
                    serviceID, "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO_sendungsDatenNull).usingRecursiveComparison().ignoringFields("message")
                    .isEqualTo(expectedWlsExceptionDTO_sendungsDatenNull);
        }
    }

    @Nested
    class PostSchnellmeldungDruckuhrzeit {

        @Test
        void should_notThrowAnyException_when_requestParamValid() throws Exception {
            val druckDatenDTO_valid = createSendungsdatenDTO("wahlID", "wahlbezirkID");
            val request_valid_param = MockMvcRequestBuilders.post("/businessActions/schnellmeldungDruckuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(druckDatenDTO_valid));
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
        }

        @Test
        void should_throwWlsException_when_requestParamIsInvalid() throws Exception {
            val request_druckDatenNull = MockMvcRequestBuilders.post("/businessActions/schnellmeldungDruckuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(null));

            val response_druckDatenNull = mockMvc.perform(request_druckDatenNull).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO_druckDatenNull = objectMapper.readValue(response_druckDatenNull.getResponse().getContentAsString(),
                    WlsExceptionDTO.class);

            val expectedWlsExceptionDTO_druckDatenNull = new WlsExceptionDTO(WlsExceptionCategory.F, ExceptionKonstanten.CODE_HTTP_MESSAGE_NOT_READABLE,
                    serviceID, "");

            Assertions.assertThat(responseBodyAsWlsExceptionDTO_druckDatenNull).usingRecursiveComparison().ignoringFields("message")
                    .isEqualTo(expectedWlsExceptionDTO_druckDatenNull);
        }
    }

    @Nested
    class PostNiederschriftSendungsuhrzeit {

        @Test
        void should_notThrowAnyException_when_requestParamValid() throws Exception {
            val sendungsDatenDTO_valid = createSendungsdatenDTO("wahlID", "wahlbezirkID");
            val request_valid_param = MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(sendungsDatenDTO_valid));
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
        }

        @Test
        void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
            val sendungsDatenDTO_wahlID_blank = createSendungsdatenDTO("  ", "wahlbezirkID");

            val request_wahlID_blank = MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(sendungsDatenDTO_wahlID_blank));

            val response_wahlID_blank = mockMvc.perform(request_wahlID_blank).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO_wahlID_blank = objectMapper.readValue(response_wahlID_blank.getResponse().getContentAsString(),
                    WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
                    serviceID, ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO_wahlID_blank).usingRecursiveComparison().ignoringFields("message")
                    .isEqualTo(expectedWlsExceptionDTO);
        }
    }

    @Nested
    class PostNiederschriftDruckuhrzeit {

        @Test
        void should_notThrowAnyException_when_requestParamValid() throws Exception {
            val druckDatenDTO_valid = createSendungsdatenDTO("wahlID", "wahlbezirkID");
            val request_valid_param = MockMvcRequestBuilders.post("/businessActions/niederschriftSendungsuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(druckDatenDTO_valid));
            Assertions.assertThatNoException().isThrownBy(() -> mockMvc.perform(request_valid_param));
        }

        @Test
        void should_throwWlsException_when_requestParamsAreInvalid() throws Exception {
            val druckDatenDTO_wahlbezirkID_empty = createDruckdatenDTO("wahlID", "");
            val request_wahlbezirkID_empty = MockMvcRequestBuilders.post("/businessActions/niederschriftDruckuhrzeit").with(csrf())
                    .contentType(MediaType.APPLICATION_JSON).content(
                            objectMapper.writeValueAsString(druckDatenDTO_wahlbezirkID_empty));

            val response_wahlbezirkID_empty = mockMvc.perform(request_wahlbezirkID_empty).andExpect(status().isBadRequest()).andReturn();
            val responseBodyAsWlsExceptionDTO_wahlbezirkID_empty = objectMapper.readValue(response_wahlbezirkID_empty.getResponse().getContentAsString(),
                    WlsExceptionDTO.class);

            val expectedWlsExceptionDTO = new WlsExceptionDTO(WlsExceptionCategory.F,
                    ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG.code(),
                    serviceID, ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG.message());

            Assertions.assertThat(responseBodyAsWlsExceptionDTO_wahlbezirkID_empty).usingRecursiveComparison().ignoringFields("message")
                    .isEqualTo(expectedWlsExceptionDTO);
        }

    }

    private SendungsdatenDTO createSendungsdatenDTO(final String wahlID, final String wahlbezirkID) {
        return SendungsdatenDTO.builder().bezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID)).build();
    }

    private DruckdatenDTO createDruckdatenDTO(final String wahlID, final String wahlbezirkID) {
        return DruckdatenDTO.builder().bezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID)).build();
    }
}
