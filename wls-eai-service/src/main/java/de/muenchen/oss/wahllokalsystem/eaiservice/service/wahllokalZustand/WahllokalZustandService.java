package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustand;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahllokalZustandService {

  private final WahllokalZustandValidator wahllokalZustandValidator;
  private final WahllokalZustandMapper wahllokalZustandMapper;
  private final WahllokalZustandRepository wahllokalZustandRepository;
  private final ExceptionFactory exceptionFactory;

  @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveWahllokalZustand')")
  public void setWahllokalZustand(final WahllokalZustandDTO wahllokalZustandToSet) {
    wahllokalZustandValidator.validWahllokalZustandOrThrow(wahllokalZustandToSet);
    wahllokalZustandRepository.save(wahllokalZustandMapper.toEntity(wahllokalZustandToSet));
  }

  @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveWahllokalZustand')")
  public void setWahllokalZustandLastSeen(
      final String wahlbezirkID, final String teamID, final LocalDateTime timestamp) {
    if (StringUtils.isBlank(wahlbezirkID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT);
    }

    if (StringUtils.isBlank(teamID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_TEAMID_FEHLT);
    }

    if (timestamp == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_TIMESTAMP_FEHLT);
    }

    final UUID parsedWahlbezirkID;
    try {
      parsedWahlbezirkID = UUID.fromString(wahlbezirkID);
    } catch (IllegalArgumentException e) {
      throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
    }

    wahllokalZustandRepository.save(
        WahllokalZustand.builder()
            .zuletztGesehen(timestamp)
            .wahlbezirkID(parsedWahlbezirkID)
            .teamID(teamID)
            .build());
  }

  @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveWahllokalZustand')")
  public void setWahllokalZustandLetzteAbmeldung(
      final String wahlbezirkID, final String teamID, final LocalDateTime timestamp) {
    if (StringUtils.isBlank(wahlbezirkID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT);
    }

    if (StringUtils.isBlank(teamID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_TEAMID_FEHLT);
    }

    if (timestamp == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_TIMESTAMP_FEHLT);
    }

    final UUID parsedWahlbezirkID;
    try {
      parsedWahlbezirkID = UUID.fromString(wahlbezirkID);
    } catch (IllegalArgumentException e) {
      throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
    }

    wahllokalZustandRepository.save(
        WahllokalZustand.builder()
            .letzteAbmeldung(timestamp)
            .wahlbezirkID(parsedWahlbezirkID)
            .teamID(teamID)
            .build());
  }
}
