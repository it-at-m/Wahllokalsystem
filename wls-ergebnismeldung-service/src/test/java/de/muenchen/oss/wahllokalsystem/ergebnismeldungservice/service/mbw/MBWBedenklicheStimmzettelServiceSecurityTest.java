package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.MicroServiceApplication;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.TestConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.mbw.BedenklicheStimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.utils.Authorities;
import de.muenchen.oss.wahllokalsystem.wls.common.security.BezirkIDPermissionEvaluator;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import de.muenchen.oss.wahllokalsystem.wls.common.testing.SecurityUtils;
import java.util.Collections;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.wiremock.spring.EnableWireMock;

@SpringBootTest(classes = MicroServiceApplication.class)
@EnableWireMock
@ActiveProfiles({TestConstants.SPRING_TEST_PROFILE})
class MBWBedenklicheStimmzettelServiceSecurityTest {

  @MockitoBean BezirkIDPermissionEvaluator bezirkIDPermissionEvaluator;

  @Autowired BedenklicheStimmzettelRepository bedenklicheStimmzettelRepository;

  @Autowired MBWBedenklicheStimmzettelService unitUnderTest;

  @AfterEach
  void teardown() {
    bedenklicheStimmzettelRepository.deleteAll();
  }

  @Nested
  class GetBedenklicheStimmzettelOrderedByOrderIndexAsc {

    private static final String REQUIRED_AUTHORITY =
        Authorities.SERVICE_GET_BEDENKLICHE_STIMMZETTEL;

    @Test
    void should_throwNoException_when_authorityIsGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)));
    }

    @Test
    void should_throwException_when_authorityIsNotGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY + "sthElse");
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_authorityIsGivenButWahlbezirkIDDoesNotMatch() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.getBedenklicheStimmzettelOrderedByOrderIndexAsc(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class SetBedenklicheStimmzettel {

    private static final String REQUIRED_AUTHORITY =
        Authorities.SERVICE_SET_BEDENKLICHE_STIMMZETTEL;

    @Test
    void should_throwNoException_when_authorityIsGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), Collections.emptyList()));
    }

    @Test
    void should_throwException_when_authorityIsNotGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY + "sthElse");
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), Collections.emptyList()))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_authorityIsGivenButWahlbezirkIDDoesNotMatch() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.setBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID), Collections.emptyList()))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class HasBedenklicheStimmzettel {
    private static final String REQUIRED_AUTHORITY =
        Authorities.SERVICE_GET_BEDENKLICHE_STIMMZETTEL;

    @Test
    void should_throwNoException_when_authorityIsGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.hasBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)));
    }

    @Test
    void should_throwException_when_authorityIsNotGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY + "sthElse");
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.hasBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_authorityIsGivenButWahlbezirkIDDoesNotMatch() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.hasBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }
  }

  @Nested
  class GetAnzahlUngueltigeBedenklicheStimmzettel {
    private static final String REQUIRED_AUTHORITY =
        Authorities.SERVICE_GET_BEDENKLICHE_STIMMZETTEL;

    @Test
    void should_throwNoException_when_authorityIsGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatNoException()
          .isThrownBy(
              () ->
                  unitUnderTest.getAnzahlUngueltigeBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)));
    }

    @Test
    void should_throwException_when_authorityIsNotGiven() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(true);

      SecurityUtils.runWith(REQUIRED_AUTHORITY + "sthElse");
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.getAnzahlUngueltigeBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void should_throwException_when_authorityIsGivenButWahlbezirkIDDoesNotMatch() {
      val wahlbezirkID = "wahlbezirkID";
      val wahlID = "wahlID";

      Mockito.when(
              bezirkIDPermissionEvaluator.tokenUserBezirkIdMatches(
                  Mockito.eq(wahlbezirkID), Mockito.any()))
          .thenReturn(false);

      SecurityUtils.runWith(REQUIRED_AUTHORITY);
      Assertions.assertThatException()
          .isThrownBy(
              () ->
                  unitUnderTest.getAnzahlUngueltigeBedenklicheStimmzettel(
                      new BezirkUndWahlID(wahlID, wahlbezirkID)))
          .isInstanceOf(AccessDeniedException.class);
    }
  }
}
