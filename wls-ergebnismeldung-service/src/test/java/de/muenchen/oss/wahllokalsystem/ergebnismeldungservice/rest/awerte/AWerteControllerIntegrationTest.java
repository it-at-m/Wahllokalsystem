package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client.eai.AWerteClientMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AsyncAWerteService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE, Profiles.DUMMY_CLIENTS})
public class AWerteControllerIntegrationTest {

  @Configuration
  static class TestConfiguration {
    @Primary
    @Bean
    public TaskExecutor syncTaskExecutor() {
      return new SyncTaskExecutor();
    }
  }

  @Autowired ObjectMapper objectMapper;

  @Autowired AWerteDTOMapper aWerteDTOMapper;

  @Autowired AWerteModelMapper aWerteModelMapper;

  @Autowired AWerteClientMapper aWerteClientMapper;

  @Autowired MockMvc api;

  @Autowired AWerteRepository awerteRepository;

  @MockitoSpyBean AsyncAWerteService asyncAWerteService;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_AWERTE);
    awerteRepository.deleteAll();
  }

  @Nested
  class GetAWerte {

    @Test
    void should_returnAWerteListFromEAI_when_dataFound() throws Exception {
      val wahlbezirkID = "wahlbezirkID1";
      // same values as DummyClientImpl
      val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);

      val response =
          api.perform(createGetRequest(wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();

      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(),
              de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte.AWerteDTO[].class);
      val expectedResponseBody =
          aWerteDTOMapper.fromListOfAWerteModelToListOfAWerteDTO(
              aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(
                  eaiWahlberechtigte));

      Assertions.assertThat(responseBodyAsDTO)
          .containsExactlyInAnyOrderElementsOf(expectedResponseBody);
    }

    @Test
    void should_saveAWerteListFromEAIToRepo_when_dataFound() throws Exception {
      val wahlbezirkID = "wahlbezirkID1";
      // same values as DummyClientImpl
      val eaiWahlberechtigte = createClientListOfAWahlberechtigteDTO(wahlbezirkID);

      api.perform(createGetRequest(wahlbezirkID, wahlbezirkID))
          .andExpect(status().isOk())
          .andReturn();

      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_USER_GET_AWERTE);
      val aWerteFromRepo = awerteRepository.findByBezirkUndWahlID_WahlbezirkID(wahlbezirkID);
      val expectedEntities =
          aWerteModelMapper.fromListOfAWerteModelToListOfAWerteEntity(
              aWerteClientMapper.fromRemoteClientListOfWahlberechtigteDtoToListOfAWerteModel(
                  eaiWahlberechtigte));

      Assertions.assertThat(aWerteFromRepo).usingRecursiveComparison().isEqualTo(expectedEntities);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      api.perform(createGetRequest(wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get("/businessActions/awerte/" + wahlbezirkID)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_AWERTE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_AWERTE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_AWERTE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class InitialiseAWerte {
    @Test
    void should_callAsynchronousMethodInitialiseAWerteAndReturnWithOK_when_wahlbezirkIDsAreGiven()
        throws Exception {
      val wahlbezirkIDs = List.of("wahlbezirkID1", "wahlbezirkID2", "wahlbezirkID3");

      val request =
          MockMvcRequestBuilders.post("/businessActions/awerte/init")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(Authorities.ADMIN_LOADWAHLTERMINDATEN)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(wahlbezirkIDs));

      api.perform(request).andExpect(status().isOk()).andReturn();
      Mockito.verify(asyncAWerteService).initialiseAWerte(wahlbezirkIDs);
    }
  }

  private List<WahlberechtigteDTO> createClientListOfAWahlberechtigteDTO(String wahlbezirkID) {
    val wahlberechtigte1 = new WahlberechtigteDTO();
    wahlberechtigte1.setWahlID("wahlID01");
    wahlberechtigte1.setWahlbezirkID(wahlbezirkID);
    wahlberechtigte1.setA1(25L);
    wahlberechtigte1.setA2(26L);
    return List.of(wahlberechtigte1);
  }
}
