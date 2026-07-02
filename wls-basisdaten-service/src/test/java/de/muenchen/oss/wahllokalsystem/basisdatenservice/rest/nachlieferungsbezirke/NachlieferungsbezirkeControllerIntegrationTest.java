package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.nachlieferungsbezirke;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.Nachlieferungsbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.NachlieferungsbezirkeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@EnableWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class NachlieferungsbezirkeControllerIntegrationTest {

  @Value("${service.info.oid}")
  String serviceID;

  @Autowired MockMvc mockMvc;

  @Autowired NachlieferungsbezirkeRepository nachlieferungsbezirkeRepository;

  @Autowired ObjectMapper objectMapper;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_NACHLIEFERUNGSBEZIRKE);
    nachlieferungsbezirkeRepository.deleteAll();
  }

  @Nested
  class IsNachlieferungsbezirk {

    @Test
    void should_returnTrue_when_nachlieferungsbezirkExistsForWahltag() throws Exception {
      val wahltagID = "wahltagID1";
      val wahlbezirkID = "wahlbezirkID1";
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_NACHLIEFERUNGSBEZIRKE);
      nachlieferungsbezirkeRepository.save(
          new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID)));

      val response =
          mockMvc
              .perform(buildGetRequest(wahltagID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo("true");
    }

    @Test
    void should_returnFalse_when_nachlieferungsbezirkNotExistsForWahltag() throws Exception {
      val wahltagID = "wahltagID1";
      val wahlbezirkID = "wahlbezirkID1";

      val response =
          mockMvc
              .perform(buildGetRequest(wahltagID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).isEqualTo("false");
    }

    @Test
    void should_returnException_when_wahltagIdIsEmpty() throws Exception {
      val wahltagID = " ";
      val wahlbezirkID = "wahlbezirkID1";

      val response =
          mockMvc
              .perform(buildGetRequest(wahltagID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isBadRequest())
              .andReturn();

      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG.code(),
              serviceID,
              ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkIdForIsNachlieferungsbezirk()
        throws Exception {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      mockMvc
          .perform(buildGetRequest(wahltagID, wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder buildGetRequest(
        final String wahltagID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(
              "/businessActions/nachlieferungsbezirke/" + wahltagID + "/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_NACHLIEFERUNGSBEZIRKE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class SetNachlieferungsbezirke {

    @Test
    void should_setNachlieferungsbezirke_when_postRequestIsSent() throws Exception {
      val wahltagID = "wahltagID";
      val wahlbezirkID = "wahlbezirkID";

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE);
      val nachlieferungsbezirkeBeforeRequest = nachlieferungsbezirkeRepository.count();
      Assertions.assertThat(nachlieferungsbezirkeBeforeRequest).isEqualTo(0L);

      mockMvc
          .perform(buildPostRequest(wahltagID, List.of(wahlbezirkID)))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE);
      val nachlieferungsbezirkeAfterRequest = nachlieferungsbezirkeRepository.count();
      Assertions.assertThat(nachlieferungsbezirkeAfterRequest).isEqualTo(1L);

      Assertions.assertThat(
              nachlieferungsbezirkeRepository
                  .findById(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID))
                  .isPresent())
          .isEqualTo(true);
    }

    @Test
    void should_overrideNachlieferungsbezirke_when_twoPostRequestsAreSent() throws Exception {
      val wahltagID = "wahltagID";
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";
      val wahlbezirkID3 = "wahlbezirkID3";

      mockMvc
          .perform(buildPostRequest(wahltagID, List.of(wahlbezirkID1)))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE);
      val nachlieferungsbezirkeAfterFirstRequest = nachlieferungsbezirkeRepository.count();
      Assertions.assertThat(nachlieferungsbezirkeAfterFirstRequest).isEqualTo(1L);

      mockMvc
          .perform(buildPostRequest(wahltagID, List.of(wahlbezirkID2, wahlbezirkID3)))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE);
      val nachlieferungsbezirkeAfterSecondRequest = nachlieferungsbezirkeRepository.count();
      Assertions.assertThat(nachlieferungsbezirkeAfterSecondRequest).isEqualTo(2L);

      Assertions.assertThat(
              nachlieferungsbezirkeRepository
                  .findById(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID2))
                  .isPresent())
          .isEqualTo(true);
      Assertions.assertThat(
              nachlieferungsbezirkeRepository
                  .findById(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID3))
                  .isPresent())
          .isEqualTo(true);
      Assertions.assertThat(
              nachlieferungsbezirkeRepository
                  .findById(new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID1))
                  .isPresent())
          .isEqualTo(false);
    }

    @Test
    void should_returnException_when_wahltagIdIsEmpty() throws Exception {
      val wahltagID = " ";
      val wahlbezirkID = "wahlbezirkID";

      val response =
          mockMvc
              .perform(buildPostRequest(wahltagID, List.of(wahlbezirkID)))
              .andExpect(status().isBadRequest())
              .andReturn();

      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.F,
              ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG.code(),
              serviceID,
              ExceptionConstants.CODE_GETWAHLBEZIRKE_PARAMETER_UNVOLLSTAENDIG.message());
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    private MockHttpServletRequestBuilder buildPostRequest(
        final String wahltagID, final List<String> nachlieferungsbezirke) {
      val header = "WahlbezirkID\n";
      val csvContent = header + String.join("\n", nachlieferungsbezirke);
      val multiPartFile =
          new MockMultipartFile(
              "file",
              "nachlieferungsbezirke.csv",
              MediaType.TEXT_PLAIN_VALUE,
              csvContent.getBytes());
      return MockMvcRequestBuilders.multipart("/businessActions/nachlieferungsbezirke/" + wahltagID)
          .file(multiPartFile)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_POST_NACHLIEFERUNGSBEZIRKE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_NACHLIEFERUNGSBEZIRKE),
                      new SimpleGrantedAuthority(
                          Authorities.REPOSITORY_DELETE_NACHLIEFERUNGSBEZIRKE),
                      new SimpleGrantedAuthority(
                          Authorities.REPOSITORY_WRITE_NACHLIEFERUNGSBEZIRKE)));
    }
  }
}
