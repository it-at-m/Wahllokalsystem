package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.mbw;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicherStimmzettel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.Supplement;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.Validity;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicheStimmzettelModelMapper;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.BedenklicherStimmzettelModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(
    classes = MicroServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class MBWBedenklicheStimmzettelControllerIntegrationTest {

  @Autowired MockMvc api;

  @Autowired ObjectMapper objectMapper;

  @Autowired BedenklicheStimmzettelRepository bedenklicheStimmzettelRepository;

  @Autowired BedenklicheStimmzettelModelMapper bedenklicheStimmzettelModelMapper;

  @Autowired BedenklicherStimmzettelDTOMapper bedenklicherStimmzettelDTOMapper;

//  @Autowired
//  TransactionTemplate transactionTemplate;

  @AfterEach
  void teardown() {
//    transactionTemplate.executeWithoutResult(status -> {
      bedenklicheStimmzettelRepository.deleteAll();
//    });
  }

  @Nested
  class GetBedenklicheStimmzettelByOrderIndexAsc {

    @Test
    void
        should_returnCollectionOfBedenklicheStimmzettelOrderedByOrderIndex_when_bedenklicheStimmzettelExist()
            throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      final var entityToFind = createEntityToFind(wahlID, wahlbezirkID);
      bedenklicheStimmzettelRepository.save(entityToFind);

      val request = createGetRequest(wahlID, wahlbezirkID, wahlbezirkID);
      val response = api.perform(request).andExpect(status().isOk()).andReturn();
      val responseBodyDTO =
          objectMapper.readValue(
              response.getResponse().getContentAsString(), BedenklicherStimmzettelDTO[].class);

      val expectedResponse = createExpectedResponseBody();
      Assertions.assertThat(responseBodyDTO).isEqualTo(expectedResponse);
    }

    @Test
    void should_return204_when_noBedenklicheStimmzettelExist() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val request = createGetRequest(wahlID, wahlbezirkID, wahlbezirkID);
      api.perform(request).andExpect(status().isNoContent());
    }

    @Test
    void should_returnForbidden_when_userHasWrongBezirkId() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      val request = createGetRequest(wahlID, wahlbezirkID, wahlbezirkID + "sthElse");
      api.perform(request).andExpect(status().isForbidden());
    }

    private BedenklicherStimmzettelDTO[] createExpectedResponseBody() {
      val bedenklicherStimmzettel1 =
          new BedenklicherStimmzettelDTO(2, Collections.emptySet(), ValidityDTO.VALID);
      val bedenklicherStimmzettel2 =
          new BedenklicherStimmzettelDTO(1, Collections.emptySet(), ValidityDTO.INVALID);
      val bedenklicherStimmzettel3 =
          new BedenklicherStimmzettelDTO(
              0,
              Set.of(
                  SupplementDTO.TOO_MANY_LISTENKREUZE,
                  SupplementDTO.TOO_MANY_SINGLE_KANDIDAT_VOTES),
              ValidityDTO.PARTIAL_VALID);
      return new BedenklicherStimmzettelDTO[] {
        bedenklicherStimmzettel3, bedenklicherStimmzettel2, bedenklicherStimmzettel1
      };
    }

    private @NonNull BedenklicheStimmzettel createEntityToFind(
        final String wahlID, final String wahlbezirkID) {
      val bedenklicherStimmzettel1 =
          new BedenklicherStimmzettel(2, Collections.emptySet(), Validity.VALID);
      val bedenklicherStimmzettel2 =
          new BedenklicherStimmzettel(1, Collections.emptySet(), Validity.INVALID);
      val bedenklicherStimmzettel3 =
          new BedenklicherStimmzettel(
              0,
              Set.of(Supplement.TOO_MANY_LISTENKREUZE, Supplement.TOO_MANY_SINGLE_KANDIDAT_VOTES),
              Validity.PARTIAL_VALID);
      return new BedenklicheStimmzettel(
          new BezirkUndWahlID(wahlID, wahlbezirkID),
          List.of(bedenklicherStimmzettel1, bedenklicherStimmzettel2, bedenklicherStimmzettel3));
    }

    private RequestBuilder createGetRequest(
        final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID) {
      return MockMvcRequestBuilders.get(createUrl(wahlbezirkID, wahlID))
          .with(
              jwt()
                  .authorities(
                      new SimpleGrantedAuthority(Authorities.SERVICE_GET_BEDENKLICHE_STIMMZETTEL))
                  .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)));
    }
  }

  @Nested
  class SetBedenklicheStimmzettel {

    @Test
    void should_saveEreignisse_when_newDataIsPosted() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val requestBody = createRequestBody();

      val request = createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody);
      api.perform(request).andExpect(status().isCreated());

      val entity = bedenklicheStimmzettelRepository.findById(new BezirkUndWahlID(wahlID, wahlbezirkID));
      final Collection<BedenklicherStimmzettelModel> requestBodyAsModel = new ArrayList<>();
      requestBody.forEach(bedenklicherStimmzettel -> {
        requestBodyAsModel.add(bedenklicherStimmzettelDTOMapper.toModel(bedenklicherStimmzettel));
      });
      val expectedEntity = bedenklicheStimmzettelModelMapper.toEntity(requestBodyAsModel, wahlbezirkID, wahlID);
      Assertions.assertThat(entity.get())
              .usingRecursiveComparison()
              .ignoringCollectionOrder()
              .isEqualTo(expectedEntity);
    }

    @Test
    void should_overrideExistingEreignisse_when_newDataIsPosted() throws Exception {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";
      val requestBody = createRequestBody();

//      transactionTemplate.executeWithoutResult((transactionStatus -> {
//        val bedenklicheStimmzettel = new ArrayList<BedenklicherStimmzettel>();
//        bedenklicheStimmzettel.add(new BedenklicherStimmzettel(0, Collections.emptySet(), Validity.VALID));
//        val entityToReplace = new BedenklicheStimmzettel(new BezirkUndWahlID(wahlID, wahlbezirkID), null);
//        bedenklicheStimmzettelRepository.save(entityToReplace);
//      }));


      val request = createPostRequest(wahlID, wahlbezirkID, wahlbezirkID, requestBody);
      api.perform(request).andExpect(status().isCreated());

      val entity = bedenklicheStimmzettelRepository.findById(new BezirkUndWahlID(wahlID, wahlbezirkID));
      final Collection<BedenklicherStimmzettelModel> requestBodyAsModel = requestBody.stream().map(bedenklicherStimmzettelDTOMapper::toModel).toList();
      val expectedEntity = bedenklicheStimmzettelModelMapper.toEntity(requestBodyAsModel, wahlbezirkID, wahlID);
      Assertions.assertThat(entity.get())
              .usingRecursiveComparison()
              .ignoringCollectionOrder()
              .isEqualTo(expectedEntity);

      Assertions.assertThat(bedenklicheStimmzettelRepository.count()).isEqualTo(1);
    }

    private Collection<BedenklicherStimmzettelDTO> createRequestBody() {
      return List.of(
              new BedenklicherStimmzettelDTO(0, Collections.emptySet(), ValidityDTO.VALID),
              new BedenklicherStimmzettelDTO(1, Set.of(SupplementDTO.TOO_MANY_LISTENKREUZE, SupplementDTO.TOO_MANY_SINGLE_KANDIDAT_VOTES), ValidityDTO.PARTIAL_VALID),
              new BedenklicherStimmzettelDTO(2, Collections.emptySet(), ValidityDTO.INVALID)
      );
    }

    private RequestBuilder createPostRequest(
            final String wahlID, final String wahlbezirkID, final String claimWahlbezirkID, final Collection<BedenklicherStimmzettelDTO> requestBody)
    throws Exception {
      return MockMvcRequestBuilders.post(createUrl(wahlbezirkID, wahlID))
              .with(csrf())
              .with(
                      jwt()
                              .authorities(
                                      new SimpleGrantedAuthority(Authorities.SERVICE_SET_BEDENKLICHE_STIMMZETTEL))
                              .jwt(jwt -> jwt.claim("wahlbezirkID", claimWahlbezirkID)))
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(requestBody))
              ;
    }
  }

  private String createUrl(final String wahlbezirkID, final String wahlID) {
    return "/mbw/wahl/" + wahlID + "/wahlbezirk/" + wahlbezirkID + "/bedenklicheStimmzettel";
  }
}
