package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import static de.muenchen.oss.wahllokalsystem.briefwahlservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefe;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.domain.BeanstandeteWahlbriefeRepository;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.test.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Map;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class BeanstandeteWahlbriefeControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired BeanstandeteWahlbriefeRepository beanstandeteWahlbriefeRepository;

  @Nested
  class GetBeanstandeteWahlbriefe {

    @AfterEach
    void teardown() {
      SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEANSTANDETE_WAHLBRIEFE);
      beanstandeteWahlbriefeRepository.deleteAll();
    }

    @Test
    void should_returnNoContent_when_noDataFound() throws Exception {
      val request =
          get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/21")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")));

      val response = api.perform(request).andExpect(status().isNoContent()).andReturn();

      Assertions.assertThat(response.getResponse().getContentAsString()).isEmpty();
    }

    @Test
    void should_returnData_when_dataIsPresentInRepo() throws Exception {
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";

      val waehlerverzeichnissNummer1 = 1L;
      val waehlerverzeichnissNummer2 = 2L;
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE);
      val beanstandeteWahlbriefe1 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe1.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer1));
      beanstandeteWahlbriefe1.setBeanstandeteWahlbriefe(
          Map.of(
              "wahl1",
              new Zurueckweisungsgrund[] {
                Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT
              },
              "wahl2",
              new Zurueckweisungsgrund[] {Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT}));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe1);

      val beanstandeteWahlbriefe2 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe2.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer2));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe2);

      val beanstandeteWahlbriefe3 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe3.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID2, waehlerverzeichnissNummer1));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe3);

      val expectedZurueckweisungen =
          Map.of(
              "wahl1",
              new Zurueckweisungsgrund[] {
                Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT
              },
              "wahl2",
              new Zurueckweisungsgrund[] {Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT});
      val expectedResponse =
          new BeanstandeteWahlbriefeDTO(
              wahlbezirkID1, waehlerverzeichnissNummer1, expectedZurueckweisungen);

      val request =
          get("/businessActions/beanstandeteWahlbriefe/"
                  + wahlbezirkID1
                  + "/"
                  + waehlerverzeichnissNummer1)
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID1)));
      val response = api.perform(request).andExpect(status().isOk()).andReturn();

      val responseBody =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), BeanstandeteWahlbriefeDTO.class);
      Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void should_returnDataWithEmptyZurueckweisegruende_when_dataIsPresentInRepo() throws Exception {
      val wahlbezirkID1 = "wahlbezirkID1";
      val wahlbezirkID2 = "wahlbezirkID2";

      val waehlerverzeichnissNummer1 = 1L;
      val waehlerverzeichnissNummer2 = 2L;
      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE);
      val beanstandeteWahlbriefe1 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe1.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer1));
      beanstandeteWahlbriefe1.setBeanstandeteWahlbriefe(
          Map.of("wahl1", new Zurueckweisungsgrund[0], "wahl2", new Zurueckweisungsgrund[0]));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe1);

      val beanstandeteWahlbriefe2 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe2.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID1, waehlerverzeichnissNummer2));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe2);

      val beanstandeteWahlbriefe3 = new BeanstandeteWahlbriefe();
      beanstandeteWahlbriefe3.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID2, waehlerverzeichnissNummer1));
      beanstandeteWahlbriefeRepository.save(beanstandeteWahlbriefe3);

      val expectedZurueckweisungen =
          Map.of("wahl1", new Zurueckweisungsgrund[0], "wahl2", new Zurueckweisungsgrund[0]);
      val expectedResponse =
          new BeanstandeteWahlbriefeDTO(
              wahlbezirkID1, waehlerverzeichnissNummer1, expectedZurueckweisungen);

      val request =
          get("/businessActions/beanstandeteWahlbriefe/"
                  + wahlbezirkID1
                  + "/"
                  + waehlerverzeichnissNummer1)
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID1)));
      val response = api.perform(request).andExpect(status().isOk()).andReturn();

      val responseBody =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), BeanstandeteWahlbriefeDTO.class);
      Assertions.assertThat(responseBody).usingRecursiveComparison().isEqualTo(expectedResponse);
    }

    @Test
    void should_returnFachlicheWlsException_when_requestIsInvalid() throws Exception {
      val request =
          get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")));

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(WlsExceptionCategory.F, "100", "WLS-BRIEFWAHL", null);

      val response = api.perform(request).andExpect(status().isBadRequest()).andReturn();
      val wlsExceptionFromResponse =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      Assertions.assertThat(wlsExceptionFromResponse)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expectedWlsExceptionDTO);
      Assertions.assertThat(wlsExceptionFromResponse.message()).isNotNull();
    }

    @Test
    void should_returnForbidden_when_wahlBezirkIdIsWrong() throws Exception {
      val request =
          get("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_GET_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID1")));

      api.perform(request).andExpect(status().isForbidden());
    }
  }

  @Nested
  class AddBeanstandeteWahlbriefe {

    @AfterEach
    void teardown() {
      SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_BEANSTANDETE_WAHLBRIEFE);
      beanstandeteWahlbriefeRepository.deleteAll();
    }

    @Test
    void should_returnFachlicheWlsException_when_requestIsInvalid() throws Exception {
      val requestBody = BeanstandeteWahlbriefeCreateDTO.builder().build();
      val request =
          post("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID")))
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestBody));

      val expecetedWlsExceptionDTO =
          new WlsExceptionDTO(WlsExceptionCategory.F, "101", "WLS-BRIEFWAHL", null);

      val result = api.perform(request).andExpect(status().isBadRequest()).andReturn();
      val resultBodyAsWlsExceptionDTO =
          objectMapper.readValue(result.getResponse().getContentAsString(), WlsExceptionDTO.class);

      Assertions.assertThat(resultBodyAsWlsExceptionDTO)
          .usingRecursiveComparison()
          .ignoringFields("message")
          .isEqualTo(expecetedWlsExceptionDTO);
      Assertions.assertThat(resultBodyAsWlsExceptionDTO.message()).isNotNull();
    }

    @Test
    void should_setNewData_when_callingPost() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 89L;

      val zurueckweisungen =
          Map.of(
              "wahl1",
              new Zurueckweisungsgrund[] {
                Zurueckweisungsgrund.ZUGELASSEN, Zurueckweisungsgrund.UNTERSCHRIFT_FEHLT
              },
              "wahl2",
              new Zurueckweisungsgrund[] {Zurueckweisungsgrund.NICHT_WAHLBERECHTIGT});
      val requestBody = new BeanstandeteWahlbriefeCreateDTO(zurueckweisungen);
      val request =
          post("/businessActions/beanstandeteWahlbriefe/"
                  + wahlbezirkID
                  + "/"
                  + waehlerverzeichnisNummer)
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestBody));

      val expectedRepoResponse = new BeanstandeteWahlbriefe();
      expectedRepoResponse.setBezirkIDUndWaehlerverzeichnisNummer(
          new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer));
      expectedRepoResponse.setBeanstandeteWahlbriefe(zurueckweisungen);

      api.perform(request).andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_BEANSTANDETE_WAHLBRIEFE);
      val repoResponse =
          beanstandeteWahlbriefeRepository
              .findById(
                  new BezirkIDUndWaehlerverzeichnisNummer(wahlbezirkID, waehlerverzeichnisNummer))
              .orElseThrow();

      Assertions.assertThat(repoResponse)
          .usingRecursiveComparison()
          .isEqualTo(expectedRepoResponse);
    }

    @Test
    void should_returnForbidden_when_wahlBezirkIdIsWrong() throws Exception {
      val requestBody = BeanstandeteWahlbriefeCreateDTO.builder().build();
      val request =
          post("/businessActions/beanstandeteWahlbriefe/wahlbezirkID/0")
              .with(
                  jwt()
                      .authorities(
                          new SimpleGrantedAuthority(
                              Authorities.SERVICE_ADD_BEANSTANDETE_WAHLBRIEFE),
                          new SimpleGrantedAuthority(
                              Authorities.REPOSITORY_WRITE_BEANSTANDETE_WAHLBRIEFE))
                      .jwt(jwt -> jwt.claim("wahlbezirkID", "wahlbezirkID1")))
              .with(csrf())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestBody));

      api.perform(request).andExpect(status().isForbidden());
    }
  }
}
