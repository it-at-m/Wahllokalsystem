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
  class GetStapelA {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getStapelA(bezirkUndWahlID));
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
          .isThrownBy(() -> unitUnderTest.getStapelA(bezirkUndWahlID))
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
          .isThrownBy(() -> unitUnderTest.getStapelA(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetStapelB {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getStapelB(bezirkUndWahlID));
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
          .isThrownBy(() -> unitUnderTest.getStapelB(bezirkUndWahlID))
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
          .isThrownBy(() -> unitUnderTest.getStapelB(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetStapelBC {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getStapelBC(bezirkUndWahlID));
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
          .isThrownBy(() -> unitUnderTest.getStapelBC(bezirkUndWahlID))
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
          .isThrownBy(() -> unitUnderTest.getStapelBC(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetStapelD {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.AUTHORITY_WAHLVORSTAND);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getStapelD(bezirkUndWahlID));
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
          .isThrownBy(() -> unitUnderTest.getStapelD(bezirkUndWahlID))
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
          .isThrownBy(() -> unitUnderTest.getStapelD(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
