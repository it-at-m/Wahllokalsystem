package de.muenchen.oss.wahllokalsystem.adminservice.configuration.nfcconverter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class NfcFilterTest {

    @Autowired
    MockMvc api;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String BUSINESS_ACTIONS_WAHLEN = "/businessActions/wahlen/";

    @Test
    void testNfcFilter() throws Exception {
        val wahltagID = "wahltagID1";
        val wahlID = "é";

        stubFor(WireMock.post("/wahlen/wahltagID1")
                .withRequestBody(equalToJson("""
                        {
                            "wahlID": "%s"
                        }
                        """.formatted(wahlID)))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)));

        List<de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO> wahlenDTOListEai = new ArrayList<>();
        val wahl1 = new de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO();
        wahl1.wahlID(wahlID);
        wahlenDTOListEai.add(wahl1);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BUSINESS_ACTIONS_WAHLEN + wahltagID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(wahlenDTOListEai));

        api.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));

        WireMock.verify(postRequestedFor(urlEqualTo("/wahlen/" + wahltagID))
                .withRequestBody(equalToJson("""
                        {
                            "wahlID": "%s"
                        }
                        """.formatted(wahlID))));
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
