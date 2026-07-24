package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.IDConverter;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WahllokalZustandServiceTest {

  @Mock WahllokalZustandMapper wahllokalZustandMapper;

  @Mock WahllokalZustandValidator wahllokalZustandValidator;

  @Mock WahllokalZustandRepository wahllokalZustandRepository;

  @Mock ExceptionFactory exceptionFactory;

  @Mock IDConverter idConverter;

  @InjectMocks WahllokalZustandService unitUnderTest;

  @Nested
  class SetWahllokalZustand {

    @Test
    void should_callValidatorAndRepo_when_dtoIsGiven() {
      val wahllokalZustand =
          new WahllokalZustandDTO("wahlbezirkID", null, null, Collections.emptySet());

      val mockedMappedWahllokalZustand = new WahllokalZustand();
      Mockito.when(wahllokalZustandMapper.toEntity(wahllokalZustand))
          .thenReturn(mockedMappedWahllokalZustand);

      unitUnderTest.setWahllokalZustand(wahllokalZustand);

      Mockito.verify(wahllokalZustandValidator).validWahllokalZustandOrThrow(wahllokalZustand);
      Mockito.verify(wahllokalZustandRepository).save(mockedMappedWahllokalZustand);
    }
  }

  @Nested
  class SetWahllokalZustandLastSeen {

    @Test
    void should_callRepo_when_lastSeenIsGiven() {
      val timestamp = LocalDateTime.now();
      val mockedWahlbezirkAsUUID = UUID.randomUUID();

      val mockedMappedWahllokalZustand =
          new WahllokalZustand(mockedWahlbezirkAsUUID, "teamID", timestamp, null, null);
      Mockito.when(idConverter.convertIDToUUIDOrThrow("2853ba2d-baaa-49ee-93f7-a653d17d6a72"))
          .thenReturn(mockedWahlbezirkAsUUID);
      Mockito.when(
              wahllokalZustandMapper.toEntityWithLastSeen(
                  mockedWahlbezirkAsUUID, "teamID", timestamp))
          .thenReturn(mockedMappedWahllokalZustand);

      unitUnderTest.setWahllokalZustandLastSeen(
          "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "teamID", timestamp);

      Mockito.verify(wahllokalZustandRepository).save(mockedMappedWahllokalZustand);
    }

    @Test
    void should_throwException_when_wahlbezirkIdIsBlank() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("wahlbezirkID is blank");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () -> unitUnderTest.setWahllokalZustandLastSeen("   ", "teamID", LocalDateTime.now()))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_throwException_when_teamIdIsBlank() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("teamID is blank");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_TEAMID_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () ->
                  unitUnderTest.setWahllokalZustandLastSeen(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "  ", LocalDateTime.now()))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_throwException_when_timestampIsNull() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("timestamp is null");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_TIMESTAMP_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () ->
                  unitUnderTest.setWahllokalZustandLastSeen(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "teamID", null))
          .isSameAs(mockedWlsException);
    }
  }

  @Nested
  class SetWahllokalZustandLetzteAbmeldung {

    @Test
    void should_callRepo_when_letzteAbmeldungIsGiven() {
      val timestamp = LocalDateTime.now();
      val mockedWahlbezirkAsUUID = UUID.randomUUID();
      val mockedMappedWahllokalZustand =
          new WahllokalZustand(mockedWahlbezirkAsUUID, "teamID", null, timestamp, null);
      Mockito.when(idConverter.convertIDToUUIDOrThrow("2853ba2d-baaa-49ee-93f7-a653d17d6a72"))
          .thenReturn(mockedWahlbezirkAsUUID);
      Mockito.when(
              wahllokalZustandMapper.toEntityWithLetzteAbmeldung(
                  mockedWahlbezirkAsUUID, "teamID", timestamp))
          .thenReturn(mockedMappedWahllokalZustand);

      unitUnderTest.setWahllokalZustandLetzteAbmeldung(
          "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "teamID", timestamp);
      Mockito.verify(wahllokalZustandRepository).save(mockedMappedWahllokalZustand);
    }

    @Test
    void should_throwException_when_wahlbezirkIdIsBlank() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("wahlbezirkID is blank");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () ->
                  unitUnderTest.setWahllokalZustandLetzteAbmeldung(
                      "   ", "teamID", LocalDateTime.now()))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_throwException_when_teamIdIsBlank() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("teamID is blank");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_TEAMID_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () ->
                  unitUnderTest.setWahllokalZustandLetzteAbmeldung(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "  ", LocalDateTime.now()))
          .isSameAs(mockedWlsException);
    }

    @Test
    void should_throwException_when_timestampIsNull() {
      val mockedWlsException =
          FachlicheWlsException.withCode("000").buildWithMessage("timestamp is null");
      Mockito.when(
              exceptionFactory.createFachlicheWlsException(
                  ExceptionConstants.SAVEWAHLLOKALZUSTAND_TIMESTAMP_FEHLT))
          .thenReturn(mockedWlsException);

      Assertions.assertThatExceptionOfType(FachlicheWlsException.class)
          .isThrownBy(
              () ->
                  unitUnderTest.setWahllokalZustandLetzteAbmeldung(
                      "2853ba2d-baaa-49ee-93f7-a653d17d6a72", "teamID", null))
          .isSameAs(mockedWlsException);
    }
  }
}
