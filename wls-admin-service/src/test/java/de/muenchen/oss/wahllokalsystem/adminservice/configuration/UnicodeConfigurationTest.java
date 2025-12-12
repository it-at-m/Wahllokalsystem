/*
 * Copyright (c): it@M - Dienstleister für Informations- und Telekommunikationstechnik
 * der Landeshauptstadt München, 2024
 */
package de.muenchen.oss.wahllokalsystem.adminservice.configuration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getAllServeEvents;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.oss.wahllokalsystem.adminservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.adminservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen.WahlDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
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

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE})
class UnicodeConfigurationTest {

  @Autowired MockMvc api;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void should_returnComposedString_when_givenDecomposedString() throws Exception {
    val wahlID = "\u0041\u0308";

    val wahlenStubbing =
        stubFor(
            WireMock.post("/businessActions/wahlen/wahltagID1")
                .willReturn(createWireMockResponse(HttpStatus.OK)));

    List<de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO>
        wahlenDTOListEai = new ArrayList<>();
    val wahlenDTO = new de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO();
    wahlenDTO.wahlID(wahlID);
    wahlenDTOListEai.add(wahlenDTO);

    val request =
        MockMvcRequestBuilders.post("/businessActions/wahlen/wahltagID1")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(wahlenDTOListEai));

    api.perform(request).andExpect(status().isOk());

    val wahlenRequest = getAllServeEvents(ServeEventQuery.forStubMapping(wahlenStubbing)).get(0);
    List<WahlDTO> wahlenRequestList =
        objectMapper.readValue(
            wahlenRequest.getRequest().getBody(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, WahlDTO.class));
    val requestedWahlenDTO = wahlenRequestList.get(0);

    val expectedWahlenRequestBodyAsDTO =
        new de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.model.WahlDTO();
    expectedWahlenRequestBodyAsDTO.wahlID("\u00c4");

    Assertions.assertThat(requestedWahlenDTO)
        .usingRecursiveComparison()
        .ignoringActualNullFields()
        .isEqualTo(expectedWahlenRequestBodyAsDTO);
  }

  private ResponseDefinitionBuilder createWireMockResponse(final HttpStatus responseStatus) {
    return aResponse().withStatus(responseStatus.value());
  }
}
