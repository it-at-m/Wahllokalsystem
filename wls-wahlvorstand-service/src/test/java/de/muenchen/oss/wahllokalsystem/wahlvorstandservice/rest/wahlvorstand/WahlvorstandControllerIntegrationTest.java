package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.rest.wahlvorstand;

import static de.muenchen.oss.wahllokalsystem.wahlvorstandservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.admin.model.ServeEventQuery;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients.aoueai.WahlvorstandClientMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.wahlvorstand.WahlvorstandRepository;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModelMapper;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.utils.TestDataFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.time.LocalDate;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@EnableWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class WahlvorstandControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired WahlvorstandRepository wahlvorstandRepository;

  @Autowired WahlvorstandDTOMapper wahlvorstandDTOMapper;

  @Autowired WahlvorstandClientMapper wahlvorstandClientMapper;

  @Autowired WahlvorstandModelMapper wahlvorstandModelMapper;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAHLVORSTAND);
    wahlvorstandRepository.deleteAll();
  }

  @Nested
  class GetWahlvorstand {

    @Test
    @Transactional
    void should_returnFallbackWahlvorstand_when_noDataFound() throws Exception {
      val infomanagementKonfigurierterWahltag =
          TestDataFactory.CreateFromClient.konfigurierterWahltagDTO(
              LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
      WireMock.stubFor(
          WireMock.get("/businessActions/konfigurierterWahltag")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(
                          objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));

      var searchingForWahltag = infomanagementKonfigurierterWahltag.getWahltagID();
      val basisdatenWahlen = TestDataFactory.CreateFromClient.wahlModelList();
      WireMock.stubFor(
          WireMock.get("/businessActions/wahlen/" + searchingForWahltag)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(basisdatenWahlen))));

      val wahlbezirkID = "wahlbezirkID";
      val response =
          api.perform(buildGetRequest(wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      Assertions.assertThat(response.getResponse().getContentAsString()).contains("FALLBACK");
    }

    @Test
    void should_returnWahlvorstand_when_dataFound() throws Exception {
      val wahlvorstand = TestDataFactory.CreateWahlvorstandEntity.withData();
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLVORSTAND);
      wahlvorstandRepository.save(wahlvorstand);
      val mockedWahlvorstandModel =
          TestDataFactory.CreateWahlvorstandModel.fromEntity(wahlvorstand);

      val wahlbezirkID = "wahlbezirkID";
      val response =
          api.perform(buildGetRequest(wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WahlvorstandDTO.class);

      val expectedResponseDTO = wahlvorstandDTOMapper.toDTO(mockedWahlvorstandModel);
      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseDTO);
    }

    @Test
    void should_updateWahlvorstand_when_forceUpdateParamIsTrue() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val infomanagementKonfigurierterWahltag =
          TestDataFactory.CreateFromClient.konfigurierterWahltagDTO(
              LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
      WireMock.stubFor(
          WireMock.get("/businessActions/konfigurierterWahltag")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(
                          objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));
      var searchingForWahltag = infomanagementKonfigurierterWahltag.getWahltagID();
      val basisdatenWahlen = TestDataFactory.CreateFromClient.wahlModelList();
      WireMock.stubFor(
          WireMock.get("/businessActions/wahlen/" + searchingForWahltag)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(basisdatenWahlen))));
      val eaiWahlvorstandDto = TestDataFactory.CreateFromClient.wahlvorstandDto(wahlbezirkID);
      WireMock.stubFor(
          WireMock.get("/wahlvorstaende?wahlbezirkID=" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(eaiWahlvorstandDto))));

      val response =
          api.perform(buildUpdateRequest(wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WahlvorstandDTO.class);

      val expectedResponseDTO =
          wahlvorstandDTOMapper.toDTO(wahlvorstandClientMapper.toModel(eaiWahlvorstandDto));
      Assertions.assertThat(responseBodyAsDTO)
          .usingRecursiveComparison()
          .ignoringFields("wahlvorstandsmitglieder.funktionsname")
          .isEqualTo(expectedResponseDTO);
      Assertions.assertThat(responseBodyAsDTO.wahlvorstandsmitglieder())
          .allSatisfy(mitglied -> Assertions.assertThat(mitglied.funktionsname()).isNotEmpty());
    }

    @Test
    void should_returnFallbackWahlvorstand_when_forceUpdateParamIsTrueAndEaiCallFailed()
        throws Exception {
      val wahlbezirkID = "wahlbezirkID";

      val infomanagementKonfigurierterWahltag =
          TestDataFactory.CreateFromClient.konfigurierterWahltagDTO(
              LocalDate.now().plusMonths(1), KonfigurierterWahltagDTO.WahltagStatusEnum.AKTIV);
      WireMock.stubFor(
          WireMock.get("/businessActions/konfigurierterWahltag")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(
                          objectMapper.writeValueAsBytes(infomanagementKonfigurierterWahltag))));
      var searchingForWahltag = infomanagementKonfigurierterWahltag.getWahltagID();

      val basisdatenWahlen = TestDataFactory.CreateFromClient.wahlModelList();
      WireMock.stubFor(
          WireMock.get("/businessActions/wahlen/" + searchingForWahltag)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(basisdatenWahlen))));

      WireMock.stubFor(
          WireMock.get("/wahlvorstaende?wahlbezirkID=" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                      .withBody(
                          objectMapper.writeValueAsBytes(
                              new WlsExceptionDTO(
                                  WlsExceptionCategory.T,
                                  "000",
                                  "WLS-EAI-SERVICE",
                                  "error on loading wahlvorstand")))));

      val response =
          api.perform(buildUpdateRequest(wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).contains("FALLBACK");
    }

    @Test
    void should_return403WithWlsException_when_accessingWahlbezirkOfOtherUserForGetWahlvorstand()
        throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val response =
          api.perform(buildGetRequest(wahlbezirkID, wahlbezirkID + "sth"))
              .andExpect(status().isForbidden())
              .andReturn();
      val responseBodyAsWlsException =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      Assertions.assertThat(responseBodyAsWlsException.category())
          .isEqualTo(WlsExceptionCategory.S);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkIdForUpdateWahlvorstand() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val request = buildUpdateRequest(wahlbezirkID, wahlbezirkID + "sth");

      api.perform(request).andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder buildGetRequest(
        final String wahlbezirkID, final String claimWahlbezirkID) throws Exception {
      return MockMvcRequestBuilders.get("/businessActions/wahlvorstand/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_WAHLVORSTAND),
                      new SimpleGrantedAuthority(Authorities.SERVICE_UPDATE_WAHLVORSTAND),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAHLVORSTAND),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_WAHLVORSTAND))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }

    private MockHttpServletRequestBuilder buildUpdateRequest(
        final String wahlbezirkID, final String claimWahlbezirkID) throws Exception {
      return buildGetRequest(wahlbezirkID, claimWahlbezirkID).header("forceUpdate", true);
    }
  }

  @Nested
  class PostWahlvorstand {

    @Test
    void should_saveWahlvorstand_when_newDataSuccessfullySaved() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandWriteDto.withData();

      val eaiPostWahlvorstandStubbing =
          WireMock.stubFor(
              WireMock.put("/wahlvorstaende/anwesenheit")
                  .willReturn(
                      WireMock.aResponse()
                          .withHeader("Content-Type", "application/json")
                          .withStatus(HttpStatus.OK.value())));

      api.perform(buildPostRequest(wahlbezirkID, wahlbezirkID, mockedWahlvorstandDTO))
          .andExpect(status().isOk())
          .andReturn();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLVORSTAND);
      val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
      val expectedWahlvorstand =
          wahlvorstandModelMapper.toEntity(
              wahlvorstandDTOMapper.toModel(wahlbezirkID, mockedWahlvorstandDTO));
      Assertions.assertThat(wahlvorstandFromRepo)
          .usingRecursiveComparison()
          .isEqualTo(expectedWahlvorstand);

      val eaiRequests =
          WireMock.getAllServeEvents(ServeEventQuery.forStubMapping(eaiPostWahlvorstandStubbing));

      Assertions.assertThat(eaiRequests).hasSize(1);
      val receivedEaiRequest =
          objectMapper.readTree(eaiRequests.get(0).getRequest().getBodyAsString());
      val expectedEaiRequest =
          objectMapper
              .createObjectNode()
              .put("wahlbezirkID", wahlbezirkID)
              .put("anwesenheitBeginn", mockedWahlvorstandDTO.anwesenheitBeginn().toString())
              .set(
                  "mitglieder",
                  objectMapper
                      .createArrayNode()
                      .add(
                          objectMapper
                              .createObjectNode()
                              .put(
                                  "identifikator",
                                  mockedWahlvorstandDTO
                                      .wahlvorstandsmitglieder()
                                      .get(0)
                                      .identifikator())
                              .put(
                                  "anwesend",
                                  mockedWahlvorstandDTO
                                      .wahlvorstandsmitglieder()
                                      .get(0)
                                      .anwesend())));

      Assertions.assertThat(receivedEaiRequest).isEqualTo(expectedEaiRequest);
    }

    @Test
    void should_overrideOldWahlvorstand_when_newDataSuccessfullySaved() throws Exception {
      val wahlbezirkID = "wahlbezirkID";

      val wahlvorstandToOverride = TestDataFactory.CreateWahlvorstandEntity.withData();
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAHLVORSTAND);
      wahlvorstandRepository.save(wahlvorstandToOverride);
      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLVORSTAND);
      val wahlvorstandBeforeOverridden = wahlvorstandRepository.findById(wahlbezirkID).get();

      val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandWriteDto.withData();
      WireMock.stubFor(
          WireMock.put("/wahlvorstaende/anwesenheit")
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())));

      api.perform(buildPostRequest(wahlbezirkID, wahlbezirkID, mockedWahlvorstandDTO))
          .andExpect(status().isOk())
          .andReturn();

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLVORSTAND);
      val wahlvorstandFromRepo = wahlvorstandRepository.findById(wahlbezirkID).get();
      val expectedWahlvorstand =
          wahlvorstandModelMapper.toEntity(
              wahlvorstandDTOMapper.toModel(wahlbezirkID, mockedWahlvorstandDTO));

      Assertions.assertThat(wahlvorstandFromRepo)
          .usingRecursiveComparison()
          .isEqualTo(expectedWahlvorstand);
      Assertions.assertThat(wahlvorstandBeforeOverridden.getWahlvorstandsmitglieder())
          .isNotEqualTo(wahlvorstandFromRepo.getWahlvorstandsmitglieder());
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val mockedWahlvorstandDTO = TestDataFactory.CreateWahlvorstandWriteDto.withData();

      val request = buildPostRequest(wahlbezirkID, wahlbezirkID + "sth", mockedWahlvorstandDTO);

      api.perform(request).andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder buildPostRequest(
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final WahlvorstandWriteDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post("/businessActions/wahlvorstand/" + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_POST_WAHLVORSTAND),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAHLVORSTAND))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }
}
