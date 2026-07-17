package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WahllokalZustandValidator {

  private final ExceptionFactory exceptionFactory;

  public void validWahllokalZustandOrThrow(final WahllokalZustandDTO zustand) {
    if (zustand == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.DATENALLGEMEIN_PARAMETER_FEHLEN);
    }

    if (StringUtils.isBlank(zustand.wahlbezirkID())) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_WAHLBEZIRKID_FEHLT);
    }

    if (StringUtils.isBlank(zustand.teamID())) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.SAVEWAHLLOKALZUSTAND_TEAMID_FEHLT);
    }
  }
}
