package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.Stimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelID;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelOwnerModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.rest.model.WlsExceptionDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.List;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
public class StimmzettelControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @MockitoSpyBean StimmzettelRepository stimmzettelRepository;

  @Autowired StimmzettelDTOMapper stimmzettelDTOMapper;

  @Autowired StimmzettelModelMapper stimmzettelModelMapper;

  @Autowired TransactionTemplate transactionTemplate;

  @Autowired ServiceIDFormatter serviceIDFormatter;

  @AfterEach
  void teardown() {
    stimmzettelRepository.deleteAll();
  }

  @Nested
  class GetStimmzettel {

    @Test
    void should_returnHttpStatus200WithStimmzettel_when_stimmzettelExist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val stimmzettel1ToFind =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 1))
              .create();
      val stimmzettel2ToFind =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 2))
              .create();
      val stimmzettel3ToFind =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 3))
              .create();

      val stimmzettelNotToFind =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID + "sth", wahlID, teamID, 4))
              .create();

      stimmzettelRepository.saveAll(
          List.of(
              stimmzettel1ToFind, stimmzettel2ToFind, stimmzettelNotToFind, stimmzettel3ToFind));

      val performedRequest =
          api.perform(createRequest(wahlID, wahlbezirkID, teamID))
              .andExpect(status().isOk())
              .andReturn();
      val responseAsDTO =
          objectMapper.readValue(
              performedRequest.getResponse().getContentAsString(), StimmzettelOfTeamDTO[].class);

      val expectedItem1 =
          stimmzettelDTOMapper.toDTO(stimmzettelModelMapper.toModel(stimmzettel1ToFind));
      val expectedItem2 =
          stimmzettelDTOMapper.toDTO(stimmzettelModelMapper.toModel(stimmzettel2ToFind));
      val expectedItem3 =
          stimmzettelDTOMapper.toDTO(stimmzettelModelMapper.toModel(stimmzettel3ToFind));

      Assertions.assertThat(responseAsDTO).hasSize(3);
      Assertions.assertThat(responseAsDTO)
          .containsExactly(expectedItem1, expectedItem2, expectedItem3);
    }

    @Test
    void should_returnHttpStatus204WithNoData_when_noStimmzettelExists() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val stimmzettelNotToFind =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID + "sth", wahlID, teamID, 4))
              .create();

      stimmzettelRepository.saveAll(List.of(stimmzettelNotToFind));

      val performedRequest =
          api.perform(createRequest(wahlID, wahlbezirkID, teamID))
              .andExpect(status().isNoContent())
              .andReturn();

      Assertions.assertThat(performedRequest.getResponse().getContentAsString()).isEmpty();
    }

    private MockHttpServletRequestBuilder createRequest(
        final String wahlID, final String wahlbezirkID, final String teamID) {
      val uri =
          UriComponentsBuilder.fromUriString(
                  "/stimmzettelerfassung/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/stimmzettel")
              .buildAndExpand(wahlID, wahlbezirkID, teamID)
              .toUriString();
      return MockMvcRequestBuilders.get(uri)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          "Ergebnismeldung_BUSINESSACTION_GetStimmzettelOfTeam"))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)));
    }
  }

  @Nested
  class PostStimmzettel {

    @Test
    void should_addNewStimmzettel_when_notExist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val stimmzettel1ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 1)
              .create();
      val stimmzettel2ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 2)
              .create();
      val stimmzettel3ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 3)
              .create();

      api.perform(
              createRequest(
                  wahlID,
                  wahlbezirkID,
                  teamID,
                  List.of(stimmzettel1ToSave, stimmzettel2ToSave, stimmzettel3ToSave)))
          .andExpect(status().isCreated());

      transactionTemplate.executeWithoutResult(
          status -> {
            val savedStimmzettel =
                stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                    wahlbezirkID, wahlID, teamID);
            val expectedSavedStimmzettel1 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel1ToSave));
            val expectedSavedStimmzettel2 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel2ToSave));
            val expectedSavedStimmzettel3 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel3ToSave));
            Assertions.assertThat(savedStimmzettel)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(
                    List.of(
                        expectedSavedStimmzettel1,
                        expectedSavedStimmzettel2,
                        expectedSavedStimmzettel3));
            Assertions.assertThat(savedStimmzettel).hasSize(3);
          });
    }

    @Test
    void should_replaceExistingValues_when_exist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val stimmzettel1ToReplace =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 4))
              .create();
      val stimmzettel2ToReplace =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 5))
              .create();
      stimmzettelRepository.saveAll(List.of(stimmzettel1ToReplace, stimmzettel2ToReplace));

      val stimmzettel1ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 1)
              .create();
      val stimmzettel2ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 2)
              .create();
      val stimmzettel3ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 3)
              .create();

      api.perform(
              createRequest(
                  wahlID,
                  wahlbezirkID,
                  teamID,
                  List.of(stimmzettel1ToSave, stimmzettel2ToSave, stimmzettel3ToSave)))
          .andExpect(status().isCreated());

      transactionTemplate.executeWithoutResult(
          status -> {
            val savedStimmzettel =
                stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                    wahlbezirkID, wahlID, teamID);
            val expectedSavedStimmzettel1 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel1ToSave));
            val expectedSavedStimmzettel2 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel2ToSave));
            val expectedSavedStimmzettel3 =
                stimmzettelModelMapper.toEntity(
                    new StimmzettelOwnerModel(wahlbezirkID, wahlID, teamID),
                    stimmzettelDTOMapper.toModel(stimmzettel3ToSave));
            Assertions.assertThat(savedStimmzettel)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(
                    List.of(
                        expectedSavedStimmzettel1,
                        expectedSavedStimmzettel2,
                        expectedSavedStimmzettel3));
            Assertions.assertThat(savedStimmzettel).hasSize(3);
          });
    }

    @Test
    void should_keepExistingValue_when_savingOfNewValuesFailed() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);

      val stimmzettel1ToReplace =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 4))
              .create();
      val stimmzettel2ToReplace =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, teamID, 5))
              .create();
      stimmzettelRepository.saveAll(List.of(stimmzettel1ToReplace, stimmzettel2ToReplace));

      val stimmzettel1ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 1)
              .create();
      val stimmzettel2ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 2)
              .create();
      val stimmzettel3ToSave =
          Instancio.of(StimmzettelOfTeamDTO.class)
              .set(Select.field(StimmzettelOfTeamDTO::stimmzettelkennung), 3)
              .create();

      Mockito.doThrow(new RuntimeException("mocked repo exception"))
          .when(stimmzettelRepository)
          .saveAll(Mockito.anyList());
      val performedRequest =
          api.perform(
                  createRequest(
                      wahlID,
                      wahlbezirkID,
                      teamID,
                      List.of(stimmzettel1ToSave, stimmzettel2ToSave, stimmzettel3ToSave)))
              .andExpect(status().isInternalServerError())
              .andReturn();
      val responseBodyAsWlsExceptionDTO =
          objectMapper.readValue(
              performedRequest.getResponse().getContentAsString(), WlsExceptionDTO.class);

      val expectedWlsException =
          new WlsExceptionDTO(
              WlsExceptionCategory.T,
              "999",
              serviceIDFormatter.getId(),
              "Ursache: class java.lang.RuntimeException, Nachricht: mocked repo exception");
      Assertions.assertThat(responseBodyAsWlsExceptionDTO).isEqualTo(expectedWlsException);

      transactionTemplate.executeWithoutResult(
          status -> {
            val savedStimmzettel =
                stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
                    wahlbezirkID, wahlID, teamID);
            Assertions.assertThat(savedStimmzettel)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(stimmzettel1ToReplace, stimmzettel2ToReplace));
            Assertions.assertThat(savedStimmzettel).hasSize(2);
          });
    }

    private MockHttpServletRequestBuilder createRequest(
        final String wahlID,
        final String wahlbezirkID,
        final String teamID,
        final List<StimmzettelOfTeamDTO> requestBody)
        throws Exception {
      val uri =
          UriComponentsBuilder.fromUriString(
                  "/stimmzettelerfassung/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/team/{teamID}/stimmzettel")
              .buildAndExpand(wahlID, wahlbezirkID, teamID)
              .toUriString();
      return MockMvcRequestBuilders.post(uri)
          .with(csrf())
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          "Ergebnismeldung_BUSINESSACTION_WriteStimmzettelOfTeam"))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(requestBody));
    }
  }

  @Nested
  class GetAnzahlStimmzettel {

    @Test
    void should_returnNumberOfStimmzettel_when_stimmzettelExist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      val stimmzettel1ToCount =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, Instancio.create(String.class), 1))
              .create();
      val stimmzettel2ToCount =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, Instancio.create(String.class), 2))
              .create();
      val stimmzettel3ToCount =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, Instancio.create(String.class), 3))
              .create();
      val stimmzettel4ToCount =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID, Instancio.create(String.class), 4))
              .create();
      val stimmzettel1ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID + " ", wahlID, Instancio.create(String.class), 4))
              .create();
      val stimmzettel2ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID + " ", Instancio.create(String.class), 4))
              .create();
      val stimmzettel3ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(
                      wahlbezirkID + " ", wahlID + " ", Instancio.create(String.class), 4))
              .create();
      stimmzettelRepository.saveAll(
          List.of(
              stimmzettel1ToCount,
              stimmzettel1ToIgnore,
              stimmzettel2ToCount,
              stimmzettel2ToIgnore,
              stimmzettel3ToCount,
              stimmzettel3ToIgnore,
              stimmzettel4ToCount));

      val performedRequest =
          api.perform(createRequest(wahlID, wahlbezirkID)).andExpect(status().isOk()).andReturn();

      Assertions.assertThat(performedRequest.getResponse().getContentAsString()).isEqualTo("4");
    }

    @Test
    void should_returnZero_when_noStimmzettelExist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);

      val performedRequest =
          api.perform(createRequest(wahlID, wahlbezirkID)).andExpect(status().isOk()).andReturn();

      Assertions.assertThat(performedRequest.getResponse().getContentAsString()).isEqualTo("0");
    }

    @Test
    void should_returnZero_when_noMatchingStimmzettelExist() throws Exception {
      val wahlID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val teamID = Instancio.create(String.class);
      val stimmzettel1ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID + " ", wahlID, teamID, 4))
              .create();
      val stimmzettel2ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID, wahlID + " ", teamID, 4))
              .create();
      val stimmzettel3ToIgnore =
          Instancio.of(Stimmzettel.class)
              .set(
                  Select.field(Stimmzettel::getId),
                  new StimmzettelID(wahlbezirkID + " ", wahlID + " ", teamID + " ", 4))
              .create();
      stimmzettelRepository.saveAll(
          List.of(stimmzettel1ToIgnore, stimmzettel2ToIgnore, stimmzettel3ToIgnore));

      val performedRequest =
          api.perform(createRequest(wahlID, wahlbezirkID)).andExpect(status().isOk()).andReturn();

      Assertions.assertThat(performedRequest.getResponse().getContentAsString()).isEqualTo("0");
    }

    private MockHttpServletRequestBuilder createRequest(
        final String wahlID, final String wahlbezirkID) {
      val uri =
          UriComponentsBuilder.fromUriString(
                  "/stimmzettelerfassung/wahl/{wahlID}/wahlbezirk/{wahlbezirkID}/anzahlStimmzettel")
              .buildAndExpand(wahlID, wahlbezirkID)
              .toUriString();
      return MockMvcRequestBuilders.get(uri)
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(
                          "Ergebnismeldung_BUSINESSACTION_ReadCountStimmzettel"))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", wahlbezirkID)));
    }
  }
}
