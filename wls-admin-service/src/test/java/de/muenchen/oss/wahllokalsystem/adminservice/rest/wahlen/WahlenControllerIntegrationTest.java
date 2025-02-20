package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;

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
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.utils.Authorities;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE })
class WahlenControllerIntegrationTest {

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
    class GetWahlen {

        @Test
        @WithMockUser(
                authorities = { Authorities.ADMIN_GETWAHLEN }
        )
        void should_returnData_when_wahlenClientReturnsData() throws Exception {
            val wahltagID = "wahltagID";
            val wahlID = "wahlID";
            val nowDate = LocalDate.now();
            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + wahltagID);
            val mockedWahlen = List.of(
                    new WahlDTO().wahltag(nowDate)
                            .wahlart(WahlDTO.WahlartEnum.BTW)
                            .wahlID(wahlID));

            stubFor(WireMock.get("/businessActions/wahlen/" + wahltagID).willReturn(createWireMockResponse(mockedWahlen, HttpStatus.OK)));

            val response = api.perform(request).andExpect(status().isOk()).andReturn();
            val responseBody = objectMapper.readValue(response.getResponse().getContentAsString(),
                    de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlDTO[].class);

            val expectedResponseBody = new de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlDTO(wahlID, null, null, null, nowDate, WahlartDTO.BTW,
                    null);

            Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponseBody);
        }

        @Test
        @WithMockUser(
                authorities = { Authorities.ADMIN_GETWAHLEN }
        )
        void should_returnNoContent_when_wahlenClientReturnsEmptyList() throws Exception {
            val wahltagID = "wahltagID";
            val request = MockMvcRequestBuilders.get("/businessActions/wahlen/" + wahltagID);
            val mockedWahltage = List.of();

            stubFor(WireMock.get("/businessActions/wahlen/" + wahltagID).willReturn(createWireMockResponse(mockedWahltage, HttpStatus.OK)));

            api.perform(request).andExpect(status().isNoContent()).andReturn();
        }
    }

    @Nested
    class UpdateWahlen {

        @Test
        @WithMockUser(authorities = { Authorities.ADMIN_UPDATEWAHLEN })
        void should_returnOK_when_allRemoteClientsAreCalledSuccesfully() throws Exception {
            val wahltagID = "wahltagID";
            val wahlen = List.of(new de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlDTO("wahlID1", "name1", 3L, 1L, LocalDate.now(),
                    WahlartDTO.BAW, new FarbeDTO(1, 1, 1)));

            val request = post("/businessActions/wahlen/" + wahltagID).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(wahlen));

            stubFor(WireMock.post("/businessActions/wahlen/" + wahltagID).willReturn(createWireMockResponse(HttpStatus.OK)));

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
