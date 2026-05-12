package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.referendumvorlagen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumoptionDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlageDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.eai.aou.model.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Set;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(TestConstants.SPRING_TEST_PROFILE)
@AutoConfigureWireMock
public class ReferendumvorlagenServiceSecurityTest {

  @Autowired ReferendumvorlagenService referendumvorlagenService;

  @Autowired ReferendumvorlagenRepository referendumvorlagenRepository;

  @Autowired ReferendumvorlageRepository referendumvorlageRepository;

  @Autowired ObjectMapper objectMapper;

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @AfterEach
  void teardown() {
    SecurityUtils.runWith(
        Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGEN,
        Authorities.REPOSITORY_DELETE_REFERENDUMVORLAGE);
    referendumvorlagenRepository.deleteAll();
    referendumvorlageRepository.deleteAll();
  }

  @Nested
  class LoadReferendumvorlagen {

    @Test
    void should_grantAccess_when_authoritiesArePresent() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
      WireMock.stubFor(
          WireMock.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(eaiReferendumvorschlage))));

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  referendumvorlagenService.getReferendumvorlagen(
                      new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)));
      // we have to check is data is stores because access denied exceptions are caught too
      SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
      Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(1);
    }

    @Test
    void should_denyAccess_when_authoritiesAreMissing() throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(
          Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN,
          Authorities.REPOSITORY_WRITE_REFERENDUMVORLAGEN,
          Authorities.REPOSITORY_WRITE_REFERENDUMVORLAGE);

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      Assertions.assertThatThrownBy(
              () ->
                  referendumvorlagenService.getReferendumvorlagen(
                      new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
      // we have to check is data is stores because access denied exceptions are caught too
      SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
      Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(0);
    }

    @ParameterizedTest(name = "{index} - {1} missing")
    @MethodSource("getMissingRepositoryAuthoritiesVariations")
    void should_denyAccess_when_oneRepositoryAuthorityIsMissing(
        final ArgumentsAccessor argumentsAccessor) throws Exception {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(true);
      SecurityUtils.runWith(argumentsAccessor.get(0, String[].class));

      val wahlID = "wahlID";
      val wahlbezirkID = "wahlbezirkID";

      val eaiReferendumvorschlage = createClientReferendumvorlagenDTO();
      WireMock.stubFor(
          WireMock.get("/vorschlaege/referendum/" + wahlID + "/" + wahlbezirkID)
              .willReturn(
                  WireMock.aResponse()
                      .withHeader("Content-Type", "application/json")
                      .withStatus(HttpStatus.OK.value())
                      .withBody(objectMapper.writeValueAsBytes(eaiReferendumvorschlage))));

      Assertions.assertThatThrownBy(
              () ->
                  referendumvorlagenService.getReferendumvorlagen(
                      new ReferendumvorlagenReferenceModel(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
      // we have to check is data is stores because access denied exceptions are caught too
      SecurityUtils.runWith(Authorities.REPOSITORY_READ_REFERENDUMVORLAGEN);
      Assertions.assertThat(referendumvorlagenRepository.count()).isEqualTo(0);
    }

    @Test
    void should_throwException_when_givenAllAuthoritiesButWahlbezirkIDDoesNotMatch() {
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(Mockito.any(), Mockito.any()))
          .thenReturn(false);
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN);
      Assertions.assertThatExceptionOfType(AccessDeniedException.class)
          .isThrownBy(
              () ->
                  referendumvorlagenService.getReferendumvorlagen(
                      new ReferendumvorlagenReferenceModel("wahlID", "wahlbezirkID")))
          .withMessageStartingWith("Access Denied");
    }

    private static Stream<Arguments> getMissingRepositoryAuthoritiesVariations() {
      val serviceAuthoritiesWithoutServiceAuthority =
          ArrayUtils.removeElements(
              Authorities.ALL_AUTHORITIES_GET_REFERENDUMVORLAGEN,
              Authorities.SERVICE_GET_REFERENDUMVORLAGEN);
      return SecurityUtils.buildArgumentsForMissingAuthoritiesVariations(
          serviceAuthoritiesWithoutServiceAuthority);
    }

    private ReferendumvorlagenDTO createClientReferendumvorlagenDTO() {
      val dto = new ReferendumvorlagenDTO();

      dto.setStimmzettelgebietID("szgID");

      val referendumOption = new ReferendumoptionDTO();
      referendumOption.setId("optionID");
      referendumOption.setName("optionName");
      referendumOption.setPosition(1L);

      val vorlage = new ReferendumvorlageDTO();
      vorlage.setFrage("frage");
      vorlage.setKurzname("kurzname");
      vorlage.setOrdnungszahl(1L);
      vorlage.setWahlvorschlagID("wahlvorschlagID");
      vorlage.setReferendumoptionen(Set.of(referendumOption));

      dto.setReferendumvorlagen(Set.of(vorlage));

      return dto;
    }
  }
}
