package de.muenchen.oss.wahllokalsystem.monitoringservice.rest.waehleranzahl;

import static de.muenchen.oss.wahllokalsystem.monitoringservice.TestConstants.SPRING_TEST_PROFILE;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.monitoringservice.MicroServiceApplication;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class WaehleranzahlControllerIntegrationTest {

  @Value("${service.info.oid}")
  String serviceID;

  @Autowired ObjectMapper objectMapper;

  @MockitoSpyBean WaehleranzahlRepository wahleranzahlRepository;

  @Autowired WaehleranzahlDTOMapper waehleranzahlDTOMapper;

  @Autowired WaehleranzahlModelMapper waehleranzahlModelMapper;

  @Autowired MockMvc api;

  @Autowired WaehleranzahlRepository waehleranzahlRepository;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_WAEHLERANZAHL);
    waehleranzahlRepository.deleteAll();
  }

  @Nested
  class GetWahlbeteiligung {

    @Test
    void should_returnEmptyResponse_when_noDataFound() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val response =
          api.perform(createGetRequest("wahlID", wahlbezirkID, wahlbezirkID))
              .andExpect(status().isNoContent())
              .andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      api.perform(createGetRequest("wahlID", wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    @Test
    void should_returnOkAndData_when_dataFound() throws Exception {
      val wahlID = "wahlID01";
      val wahlbezirkID = "wahlbezirkID01";
      val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
      val anzahlWaehler = 99;
      val uhrzeit = LocalDateTime.parse("2024-09-13T12:11:21.343");

      val waehleranzahlToFind = new Waehleranzahl(bezirkUndWahlID, anzahlWaehler, uhrzeit);
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL);
      waehleranzahlRepository.save(waehleranzahlToFind);

      val response =
          api.perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WaehleranzahlDTO.class);

      val expectedResponseBody =
          waehleranzahlDTOMapper.toDTO(waehleranzahlModelMapper.toModel(waehleranzahlToFind));
      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(
              "/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_WAEHLERANZAHL),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_WAEHLERANZAHL))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }

    @Nested
    class PostWahlbeteiligung {

      @Test
      void should_overwriteExistingData_when_newDataIsStoredWithSameID() throws Exception {
        val wahlID = "wahlID01";
        val wahlbezirkID = "wahlbezirkID01";
        val bezirkUndWahlID = new BezirkUndWahlID(wahlID, wahlbezirkID);
        val anzahlWaehler_1 = 99L;
        val uhrzeit_1 = LocalDateTime.parse("2024-09-13T12:11:21.343");

        // store data with bezirkUndWahlID
        val waehleranzahlToFind = new Waehleranzahl(bezirkUndWahlID, anzahlWaehler_1, uhrzeit_1);
        SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL);
        waehleranzahlRepository.save(waehleranzahlToFind);

        // Overwrite existing data with same bezirkUndWahlID
        val anzahlWaehler_2 = 55L;
        val uhrzeit_2 = LocalDateTime.parse("2024-09-13T12:11:21.666");
        val waehleranzahlDTO_2 = new WaehleranzahlDTO(anzahlWaehler_2, uhrzeit_2);

        val request_2 = buildPostRequest(wahlID, wahlbezirkID, wahlbezirkID, waehleranzahlDTO_2);
        api.perform(request_2).andExpect(status().isOk()).andReturn();

        SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAEHLERANZAHL);
        val waehleranzahlFromRepo_2 = waehleranzahlRepository.findById(bezirkUndWahlID).get();
        val expectedWaehleranzahl_2 =
            waehleranzahlModelMapper.toEntity(
                waehleranzahlDTOMapper.toSetModel(bezirkUndWahlID, waehleranzahlDTO_2));

        Assertions.assertThat(waehleranzahlFromRepo_2)
            .usingRecursiveComparison()
            .isEqualTo(expectedWaehleranzahl_2);
      }

      @Test
      void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
        val wahlID = "wahlID";
        val wahlbezirkID = "wahlbezirkID";
        val anzahlWaehler = 99L;
        val uhrzeit = LocalDateTime.parse("2024-09-13T12:11:21.343");
        val waehleranzahlDTO = new WaehleranzahlDTO(anzahlWaehler, uhrzeit);

        api.perform(buildPostRequest(wahlID, wahlbezirkID, wahlbezirkID + "sth", waehleranzahlDTO))
            .andExpect(status().isForbidden());
      }
    }

    @Test
    void should_throwTechnischeWlsException_when_saveInDBFails() throws Exception {
      val wahlID = "_   ";
      val wahlbezirkID = "wahlbezirkID01";
      val anzahlWaehler = 99L;
      val uhrzeit = LocalDateTime.parse("2024-09-13T12:11:21.343");
      val waehleranzahlDTO = new WaehleranzahlDTO(anzahlWaehler, uhrzeit);

      val request = buildPostRequest(wahlID, wahlbezirkID, wahlbezirkID, waehleranzahlDTO);

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL);
      Mockito.doThrow(new RuntimeException("DB-Error")).when(wahleranzahlRepository).save(any());

      val response = api.perform(request).andExpect(status().isInternalServerError()).andReturn();

      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsByteArray(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE.code(),
              serviceID,
              ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE.message());

      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    private RequestBuilder buildPostRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final WaehleranzahlDTO requestBody)
        throws Exception {
      return post("/businessActions/wahlbeteiligung/" + wahlID + "/" + wahlbezirkID)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_POST_WAEHLERANZAHL),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAEHLERANZAHL))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }
}
