package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlvorschlag;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.WahlvorschlaegeClientMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlag.WahlvorschlaegeModelMapper;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlvorschlag.WahlvorschlaegeValidator;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Set;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@EnableWireMock
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class WahlvorschlaegeControllerIntegrationTest {

  @Value("${service.info.oid}")
  String serviceID;

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired WahlvorschlaegeDTOMapper dtoMapper;

  @Autowired WahlvorschlaegeModelMapper modelMapper;

  @Autowired WahlvorschlaegeClientMapper wahlvorschlaegeClientMapper;

  @Autowired WahlvorschlaegeRepository wahlvorschlaegeRepository;

  @Autowired WahlvorschlagRepository wahlvorschlagRepository;

  @Autowired KandidatRepository kandidatRepository;

  @MockitoSpyBean WahlvorschlaegeValidator wahlvorschlaegeValidator;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_DELETE_WAHLVORSCHLAEGE);
    wahlvorschlaegeRepository.deleteAll();
  }

  @BeforeEach
  void setup() {
    WireMock.resetAllRequests();
  }

  @Nested
  class GetWahlvorschlaege {

    @Test
    void should_returnWahlvorschlaegeDTO_when_loadedFromExternal() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val eaiWahlvorschlaege = createClientWahlvorschlaegeDTO(wahlID, wahlbezirkID);
      WireMock.stubFor(
          WireMock.get("/vorschlaege/wahl/" + wahlID + "/" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(eaiWahlvorschlaege))));

      val response =
          api.perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WahlvorschlaegeDTO.class);

      val expectedResponseBody =
          dtoMapper.toDTO(wahlvorschlaegeClientMapper.toModel(eaiWahlvorschlaege));

      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
    }

    @Test
    @Transactional
    void should_returnWahlvorschlaege_when_externalDataIsPersisted() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val eaiWahlvorschlaege = createClientWahlvorschlaegeDTO(wahlID, wahlbezirkID);
      WireMock.stubFor(
          WireMock.get("/vorschlaege/wahl/" + wahlID + "/" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(eaiWahlvorschlaege))));

      api.perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID)).andExpect(status().isOk());

      SecurityUtils.runWith(Authorities.REPOSITORY_READ_WAHLVORSCHLAEGE);
      val dataFromRepo =
          wahlvorschlaegeRepository
              .findByBezirkUndWahlID(new BezirkUndWahlID(wahlID, wahlbezirkID))
              .get();

      val expectedEntity =
          modelMapper.toEntity(wahlvorschlaegeClientMapper.toModel(eaiWahlvorschlaege));

      Assertions.assertThat(dataFromRepo)
          .usingRecursiveComparison()
          .ignoringCollectionOrder()
          .ignoringFields(
              "id",
              "wahlvorschlaege.id",
              "wahlvorschlaege.wahlvorschlaeage",
              "wahlvorschlaege.kandidaten.id",
              "wahlvorschlaege.kandidaten.wahlvorschlag")
          .isEqualTo(expectedEntity);
    }

    @Test
    void should_returnWahlvorschlaegeDTO_when_loadedFromRepository() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val entityToFind =
          modelMapper.toEntity(
              wahlvorschlaegeClientMapper.toModel(
                  createClientWahlvorschlaegeDTO(wahlID, wahlbezirkID)));
      SecurityUtils.runWith(
          ArrayUtils.addAll(
              Authorities.ALL_AUTHORITIES_GET_WAHLVORSCHLAEGE,
              Authorities.REPOSITORY_WRITE_WAHLVORSCHLAG,
              Authorities.REPOSITORY_WRITE_KANDIDAT));
      val savedEntity = wahlvorschlaegeRepository.save(entityToFind);
      entityToFind
          .getWahlvorschlaege()
          .forEach(
              wahlvorschlag -> {
                wahlvorschlagRepository.save(wahlvorschlag);
                kandidatRepository.saveAll(wahlvorschlag.getKandidaten());
              });

      val response =
          api.perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isOk())
              .andReturn();
      val responseBodyAsDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WahlvorschlaegeDTO.class);

      val expectedResponseBody = dtoMapper.toDTO(modelMapper.toModel(savedEntity));

      Assertions.assertThat(responseBodyAsDTO).isEqualTo(expectedResponseBody);
      WireMock.verify(0, WireMock.anyRequestedFor(WireMock.anyUrl()));
    }

    @Test
    void should_returnTechnischeWlsException_when_noExternalDataFound() throws Exception {
      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      WireMock.stubFor(
          WireMock.get("/vorschlaege/wahl/" + wahlID + "/" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.NOT_FOUND.value())));

      val response =
          api.perform(createGetRequest(wahlID, wahlbezirkID, wahlbezirkID))
              .andExpect(status().isInternalServerError())
              .andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsExceptionDTO =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.code(),
              serviceID,
              ExceptionConstants.FAILED_COMMUNICATION_WITH_EAI.message());
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsExceptionDTO);
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      String wahlbezirkID = "wahlbezirkID";
      api.perform(createGetRequest("wahlID", wahlbezirkID, wahlbezirkID + "sth"))
          .andExpect(status().isForbidden());
    }
  }

  private de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO
      createClientWahlvorschlaegeDTO(final String wahlID, final String wahlbezirkID) {
    val stimmzettelgebietID = "stimmzettelgebietID";

    val clientWahlvorschlaegeDTO =
        new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlaegeDTO();
    clientWahlvorschlaegeDTO.setStimmzettelgebietID(stimmzettelgebietID);
    clientWahlvorschlaegeDTO.setWahlID(wahlID);
    clientWahlvorschlaegeDTO.setWahlbezirkID(wahlbezirkID);

    val wahlvorschlag1 =
        new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.WahlvorschlagDTO();
    wahlvorschlag1.setErhaeltStimmen(true);
    wahlvorschlag1.setIdentifikator("identifikator1");
    wahlvorschlag1.setKurzname("kurzname1");
    wahlvorschlag1.setOrdnungszahl(1L);

    val kandidat11 =
        new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
    kandidat11.setIdentifikator("kandidat11");
    kandidat11.setDirektkandidat(true);
    kandidat11.setEinzelbewerber(true);
    kandidat11.setName("name11");
    kandidat11.setListenposition(1L);
    kandidat11.setTabellenSpalteInNiederschrift(1L);
    kandidat11.setAnzahlNennungen(1);

    val kandidat12 =
        new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
    kandidat12.setIdentifikator("kandidat12");
    kandidat12.setDirektkandidat(false);
    kandidat12.setEinzelbewerber(false);
    kandidat12.setName("name12");
    kandidat12.setListenposition(2L);
    kandidat12.setTabellenSpalteInNiederschrift(2L);
    kandidat12.setAnzahlNennungen(1);
    wahlvorschlag1.setKandidaten(Set.of(kandidat11, kandidat12));

    val wahlvorschlag2 = new WahlvorschlagDTO();
    wahlvorschlag2.setErhaeltStimmen(false);
    wahlvorschlag2.setIdentifikator("identifikator2");
    wahlvorschlag2.setKurzname("kurzname2");
    wahlvorschlag2.setOrdnungszahl(2L);

    val kandidat21 =
        new de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.KandidatDTO();
    kandidat21.setIdentifikator("kandidat21");
    kandidat21.setDirektkandidat(true);
    kandidat21.setEinzelbewerber(true);
    kandidat21.setName("name21");
    kandidat21.setListenposition(3L);
    kandidat21.setTabellenSpalteInNiederschrift(3L);
    kandidat21.setAnzahlNennungen(2);

    val kandidat22 = new KandidatDTO();
    kandidat22.setIdentifikator("kandidat22");
    kandidat22.setDirektkandidat(false);
    kandidat22.setEinzelbewerber(false);
    kandidat22.setName("name22");
    kandidat22.setListenposition(4L);
    kandidat22.setTabellenSpalteInNiederschrift(4L);
    kandidat22.setAnzahlNennungen(3);
    wahlvorschlag2.setKandidaten(Set.of(kandidat21, kandidat22));

    val wahlvorschlaege = Set.of(wahlvorschlag1, wahlvorschlag2);
    clientWahlvorschlaegeDTO.setWahlvorschlaege(wahlvorschlaege);

    return clientWahlvorschlaegeDTO;
  }

  private MockHttpServletRequestBuilder createGetRequest(
      final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
    return MockMvcRequestBuilders.get(
            "/businessActions/wahlvorschlaege/" + wahlID + "/" + wahlbezirkID)
        .with(
            jwt()
                .authorities(
                    new SimpleGrantedAuthority(Authorities.SERVICE_GET_WAHLVORSCHLAEGE),
                    new SimpleGrantedAuthority(Authorities.REPOSITORY_WRITE_WAHLVORSCHLAEGE),
                    new SimpleGrantedAuthority(Authorities.REPOSITORY_READ_WAHLVORSCHLAEGE))
                .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
  }
}
