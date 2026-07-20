package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.teamstatus.StimmzettelerfassungTeamStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.security.TeamIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = MicroServiceApplication.class)
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
public class TeamStatusServiceSecurityTest {

  @MockitoBean TeamIDPermissionEvaluator teamIDPermissionEvaluator;

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @Autowired TeamStatusService unitUnderTest;

  @Autowired StimmzettelerfassungTeamStatusRepository teamStatusRepository;

  @AfterEach
  void teardown() {
    teamStatusRepository.deleteAll();
  }

  @Nested
  class SaveTeamStatus {

    @Test
    void should_getAccess_when_requiredAuthorityIsPresent() {
      // grant required authorities
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(true);
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, TeamErfassungStatusModel.REGISTRIERT));
    }

    @Test
    @WithMockUser
    void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(true);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, TeamErfassungStatusModel.REGISTRIERT))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButTeamIDEvaluatorReturnsFalse() {
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(false);
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, TeamErfassungStatusModel.REGISTRIERT))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButWahlbezirkIDEvaluatorReturnsFalse() {
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(false);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.saveTeamStatus(id, TeamErfassungStatusModel.REGISTRIERT))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetTeamStimmzettelerfassungStatus {

    @Test
    void should_getAccess_when_requiredAuthorityIsPresent() {
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(true);
      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatNoException().isThrownBy(() -> unitUnderTest.getTeamStatus(id));
    }

    @Test
    @WithMockUser
    void should_throwAccessDeniedException_when_requiredAuthorityIsMissing() {
      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(true);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getTeamStatus(id))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButTeamIDEvaluatorReturnsFalse() {
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);
      Mockito.when(teamIDPermissionEvaluator.tokenUserteamIdMatches(eq(teamID), notNull()))
          .thenReturn(false);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getTeamStatus(id))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButWahlbezirkIDEvaluatorReturnsFalse() {
      de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils.runWith(
          Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS);

      val teamID = Instancio.create(String.class);
      val wahlbezirkID = Instancio.create(String.class);
      val id = new WahlbezirkErfassungsteamIDModel("wahlID", wahlbezirkID, teamID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(false);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getTeamStatus(id))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
