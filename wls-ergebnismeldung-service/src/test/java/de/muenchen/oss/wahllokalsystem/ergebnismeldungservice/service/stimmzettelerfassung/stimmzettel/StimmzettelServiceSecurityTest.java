package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import static de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants.SPRING_TEST_PROFILE;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.TeamIDPermissionEvaluator;
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
class StimmzettelServiceSecurityTest {

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @MockitoBean TeamIDPermissionEvaluator teamIDPermissionEvaluator;

  @MockitoBean StimmzettelValidator stimmzettelValidator;

  @MockitoBean StimmzettelRepository stimmzettelRepository;

  @Autowired StimmzettelService unitUnderTest;

  @Nested
  class GetStimmzettel {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.SERVICE_GET_STIMMZETEL);
      Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getStimmzettel(ownerModel));
    }

    @Test
    void
        should_throwAccessDeniedException_when_requiredAuthorityIsGivenButWahlbezirkIDIsNotMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.SERVICE_GET_STIMMZETEL);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettel(ownerModel))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButTeamIDIsNotMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.SERVICE_GET_STIMMZETEL);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettel(ownerModel))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettel(ownerModel))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class SaveStimmzettel {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.SERVICE_WRITE_STIMMZETEL);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(ownerModel, stimmzettelToSave));
    }

    @Test
    void
        should_throwAccessDeniedException_when_requiredAuthorityIsGivenButWahlbezirkIDIsNotMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.SERVICE_WRITE_STIMMZETEL);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(ownerModel, stimmzettelToSave))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButTeamIDIsNotMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.SERVICE_WRITE_STIMMZETEL);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(ownerModel, stimmzettelToSave))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsNotGivenButIDsAreMatching() {
      val ownerModel = Instancio.create(StimmzettelOwnerModel.class);
      val stimmzettelToSave = Instancio.ofList(StimmzettelOfTeamModel.class).size(5).create();

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(ownerModel.wahlbezirkID()), Mockito.any()))
          .thenReturn(true);
      Mockito.when(
              teamIDPermissionEvaluator.tokenUserteamIdMatches(
                  Mockito.eq(ownerModel.teamID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith();
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveStimmzettel(ownerModel, stimmzettelToSave))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetAnzahlStimmzettel {

    @Test
    void should_getAccess_when_allRequiredAuthoritiesArePresentAndIDsAreMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(Authorities.SERVICE_COUNT_STIMMZETEL);
      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID));
    }

    @Test
    void should_throwAccessDeniedException_when_requiredAuthorityIsGivenButIDsAreNotMatching() {
      val bezirkUndWahlID = Instancio.create(BezirkUndWahlID.class);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(bezirkUndWahlID.getWahlbezirkID()), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(Authorities.SERVICE_COUNT_STIMMZETEL);
      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID))
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
          .isThrownBy(() -> unitUnderTest.getAnzahlStimmzettel(bezirkUndWahlID))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
