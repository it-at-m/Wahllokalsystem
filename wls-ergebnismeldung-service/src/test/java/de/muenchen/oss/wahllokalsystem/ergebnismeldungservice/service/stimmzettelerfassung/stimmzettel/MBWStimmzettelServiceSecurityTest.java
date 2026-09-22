package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.MBWStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles(profiles = {SPRING_TEST_PROFILE})
class MBWStimmzettelServiceSecurityTest {

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @MockitoBean StimmzettelValidator stimmzettelValidator;

  @MockitoBean MBWStimmzettelRepository mbwStimmzettelRepository;

  @Autowired MBWStimmzettelService unitUnderTest;

  @Nested
  class GetCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                          bezirkUndWahlID));
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButIDsAreNotMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                          bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .getCountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagSelected(
                          bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class CountByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                          bezirkUndWahlID));
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButIDsAreNotMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                          bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest
                      .countByWahlvorschlagIDOfStimmzettelWithExactlyOneWahlvorschlagThatHasChanges(
                          bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetKandidatVotes {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getKandidatVotes(bezirkUndWahlID));
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButIDsAreNotMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getKandidatVotes(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getKandidatVotes(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetCountUngueltige {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getCountUngueltige(bezirkUndWahlID));
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButIDsAreNotMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getCountUngueltige(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getCountUngueltige(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
