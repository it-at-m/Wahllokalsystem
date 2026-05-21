package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmabgabevermerke;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.EingenommenerWahlschein;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmabgabevermerke;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Stimmzettelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.Vermerk;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.StimmabgabevermerkeModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Testdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.List;
import java.util.UUID;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(classes = MicroServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class StimmabgabevermerkeControllerIntegrationTest {

  @Autowired StimmabgabevermerkeRepository stimmabgabevermerkeRepository;

  @Autowired StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;

  @Autowired StimmabgabevermerkeDTOMapper stimmabgabevermerkeDTOMapper;

  @Autowired ObjectMapper objectMapper;

  @Autowired MockMvc mockMvc;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.REPOSITORY_DELETE_STIMMABGABEVERMERKE);
    stimmabgabevermerkeRepository.deleteAll();
  }

  @Nested
  class GetStimmabgabevermerke {

    @Test
    void should_returnData_when_dataIsPresentInRepository() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      val entityToFind = createWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE);
      stimmabgabevermerkeRepository.save(entityToFind);

      val response =
          mockMvc
              .perform(
                  createGetRequest(waehlerverzeichnisNummer, wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse();
      val responseBodyAsDTO =
          objectMapper.readValue(response.getContentAsString(), StimmabgabevermerkeDTO.class);

      val expectedResult =
          stimmabgabevermerkeDTOMapper.toWahldatenDTO(
              stimmabgabevermerkeModelMapper.toModel(entityToFind));

      Assertions.assertThat(responseBodyAsDTO)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .isEqualTo(expectedResult);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      val wahlbezirkID = "  ";
      val wahlID = " ";
      val waehlerverzeichnisNummer = 1L;

      mockMvc
          .perform(createGetRequest(waehlerverzeichnisNummer, wahlID, wahlbezirkID, wahlbezirkID))
          .andExpect(status().isBadRequest());
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      mockMvc
          .perform(
              createGetRequest(
                  waehlerverzeichnisNummer, wahlID, wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createGetRequest(
        final Long waehlerverzeichnisNummer,
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(
              "/businessActions/stimmabgabevermerke/"
                  + wahlbezirkID
                  + "/"
                  + wahlID
                  + "/"
                  + waehlerverzeichnisNummer)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_STIMMABGABEVERMERKE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class PostStimmabgabevermerke {

    @Test
    void should_persistData_when_dataIsSent() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);
      val expectedEntity = createWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      val requestBody =
          stimmabgabevermerkeDTOMapper.toWahldatenDTO(
              stimmabgabevermerkeModelMapper.toModel(expectedEntity));

      mockMvc
          .perform(
              createPostRequest(
                  waehlerverzeichnisNummer, wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE);
      val persistedEntity = stimmabgabevermerkeRepository.findByNaturalId(id).get();

      Assertions.assertThat(persistedEntity)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          // expected object is not persistent -> has no UUIDs set
          .ignoringFieldsOfTypes(UUID.class)
          .isEqualTo(expectedEntity);
    }

    @Test
    void should_replaceExistingData_when_dataIsSent() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val waehlerverzeichnisNummer = 1L;
      val wahlID = "wahlID";

      val entityToReplace =
          Testdaten.Wahldaten.createEntity(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      SecurityUtils.runWith(Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE);
      stimmabgabevermerkeRepository.save(entityToReplace);

      val id =
          new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
              wahlbezirkID, wahlID, waehlerverzeichnisNummer);
      val expectedEntity = createWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      val requestBody =
          stimmabgabevermerkeDTOMapper.toWahldatenDTO(
              stimmabgabevermerkeModelMapper.toModel(expectedEntity));

      mockMvc
          .perform(
              createPostRequest(
                  waehlerverzeichnisNummer, wahlID, wahlbezirkID, wahlbezirkID, requestBody))
          .andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE);
      val persistedEntity = stimmabgabevermerkeRepository.findByNaturalId(id).get();

      Assertions.assertThat(persistedEntity)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          // expected object is not persistent -> has no UUIDs set
          .ignoringFieldsOfTypes(UUID.class)
          .isEqualTo(expectedEntity);

      Assertions.assertThat(stimmabgabevermerkeRepository.count()).isEqualTo(1);
    }

    @Test
    void should_returnBadRequest_when_requestIsInvalid() throws Exception {
      mockMvc
          .perform(createPostRequest(0L, "wahlID", " ", "wahlbezirkID", null))
          .andExpect(status().isBadRequest());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE);
      Assertions.assertThat(stimmabgabevermerkeRepository.count()).isEqualTo(0);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val waehlerverzeichnisNummer = 1L;

      val expectedEntity = createWahldaten(wahlbezirkID, wahlID, waehlerverzeichnisNummer);

      val requestBody =
          stimmabgabevermerkeDTOMapper.toWahldatenDTO(
              stimmabgabevermerkeModelMapper.toModel(expectedEntity));
      mockMvc
          .perform(
              createPostRequest(
                  waehlerverzeichnisNummer,
                  wahlID,
                  wahlbezirkID,
                  wahlbezirkID + "sth",
                  requestBody))
          .andExpect(status().isForbidden());
    }

    private MockHttpServletRequestBuilder createPostRequest(
        final Long waehlerverzeichnisNummer,
        final String wahlID,
        final String wahlbezirkID,
        final String claimWahlbezirkID,
        final StimmabgabevermerkeDTO requestBody)
        throws Exception {
      return MockMvcRequestBuilders.post(
              "/businessActions/stimmabgabevermerke/"
                  + wahlbezirkID
                  + "/"
                  + wahlID
                  + "/"
                  + waehlerverzeichnisNummer)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_STIMMABGABEVERMERKE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_STIMMABGABEVERMERKE),
                      new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_STIMMABGABEVERMERKE))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }

  private Stimmabgabevermerke createWahldaten(
      final String wahlbezirkID, final String wahlID, final Long waehlerverzeichnisNummer) {
    val emptyWahldaten = new Stimmabgabevermerke();
    val stimmzettel1 = new Stimmzettel();
    stimmzettel1.setAnzahl(20);
    stimmzettel1.setStimmzettelart(Stimmzettelart.KLEIN);

    val stimmzettel2 = new Stimmzettel();
    stimmzettel2.setAnzahl(21);
    stimmzettel2.setStimmzettelart(Stimmzettelart.GROSS);

    val stimmzettel3 = new Stimmzettel();
    stimmzettel3.setAnzahl(22);
    stimmzettel3.setStimmzettelart(Stimmzettelart.BEIDE);

    val vermerk1 = new Vermerk();
    vermerk1.setBlattnummer(1L);
    vermerk1.getStimmzettel().addAll(List.of(stimmzettel1, stimmzettel2, stimmzettel3));

    val vermerk2 = new Vermerk();
    vermerk2.setBlattnummer(2L);
    vermerk2.getStimmzettel().addAll(List.of(stimmzettel1, stimmzettel2, stimmzettel3));

    val wahlschein1 = new EingenommenerWahlschein();
    wahlschein1.setAnzahl(1);
    wahlschein1.setStimmzettelart(Stimmzettelart.KLEIN);
    val wahlschein2 = new EingenommenerWahlschein();
    wahlschein2.setAnzahl(2);
    wahlschein2.setStimmzettelart(Stimmzettelart.GROSS);
    val wahlschein3 = new EingenommenerWahlschein();
    wahlschein3.setAnzahl(3);
    wahlschein3.setStimmzettelart(Stimmzettelart.BEIDE);

    emptyWahldaten.setBezirkUndWahlIDUndWaehlerverzeichnisnummer(
        new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
            wahlbezirkID, wahlID, waehlerverzeichnisNummer));
    List.of(vermerk1, vermerk2).forEach(emptyWahldaten::addVermerk);
    emptyWahldaten
        .getEingenommeneWahlscheine()
        .addAll(List.of(wahlschein1, wahlschein2, wahlschein3));

    return emptyWahldaten;
  }
}
