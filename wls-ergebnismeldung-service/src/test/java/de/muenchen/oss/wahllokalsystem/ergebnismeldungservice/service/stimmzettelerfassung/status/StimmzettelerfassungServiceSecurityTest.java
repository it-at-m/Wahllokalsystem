package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import lombok.val;
import org.assertj.core.api.Assertions;
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
public class StimmzettelerfassungServiceSecurityTest {

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @Autowired StimmzettelerfassungService unitUnderTest;

  @Autowired StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;

  @AfterEach
  void teardown() {
    stimmzettelerfassungStatusRepository.deleteAll();
  }

  @Nested
  class SaveStimmzettelerfassungStatus {

    @Test
    void should_getAccess_when_requiredAuthorityIsPresent() {
      SecurityUtils.runWith(Authorities.SERVICE_SAVE_STIMMZETTELERFASSUNGSTATUS);

      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.saveStimmzettelerfassungStatus(
                      id, ErfassungStatusModel.STE_ABGESCHLOSSEN));
    }

    @Test
    @WithMockUser
    void should_throwAccessDeniedException_when_requiredPropertyIsMissing() {
      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.saveStimmzettelerfassungStatus(
                      id, ErfassungStatusModel.STE_ABGESCHLOSSEN))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
      SecurityUtils.runWith(Authorities.ALL_AUTHORITIES_GET_STIMMZETTELUMSCHLAEGE);

      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(false);

      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.saveStimmzettelerfassungStatus(
                      id, ErfassungStatusModel.STE_ABGESCHLOSSEN))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetStimmzettelerfassungStatus {
    @Test
    void should_getAccess_when_requiredAuthorityIsPresent() {
      SecurityUtils.runWith(Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS);

      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatNoException()
          .isThrownBy(() -> unitUnderTest.getStimmzettelerfassungStatus(id));
    }

    @Test
    @WithMockUser
    void should_throwAccessDeniedException_when_requiredPropertyIsMissing() {
      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(true);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettelerfassungStatus(id))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void
        should_throwAccessDeniedException_when_allRequiredAuthoritiesArePresentButBezirkIDEvaluatorReturnsFalse() {
      SecurityUtils.runWith(Authorities.SERVICE_GET_STIMMZETTELERFASSUNGSTATUS);

      val wahlbezirkID = "wahlbezirkID";
      val id = new BezirkUndWahlID("wahlID", wahlbezirkID);

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(eq(wahlbezirkID), notNull()))
          .thenReturn(false);

      Assertions.assertThatException()
          .isThrownBy(() -> unitUnderTest.getStimmzettelerfassungStatus(id))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
