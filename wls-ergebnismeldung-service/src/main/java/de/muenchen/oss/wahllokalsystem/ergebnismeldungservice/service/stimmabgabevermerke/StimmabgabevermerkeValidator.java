package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StimmabgabevermerkeValidator {

  private final ExceptionFactory exceptionFactory;

  public void validBezirkIDUndWaehlerverzeichnisnummerOrThrow(
      final BezirkUndWahlIDUndWaehlerverzeichnisnummer bezirkIDUndWaehlerverzeichnisNummer,
      final FachlicheWlsException exceptionOnInvalid)
      throws FachlicheWlsException {
    if (bezirkIDUndWaehlerverzeichnisNummer == null
        || StringUtils.isBlank(bezirkIDUndWaehlerverzeichnisNummer.getWahlbezirkID())
        || StringUtils.isBlank(bezirkIDUndWaehlerverzeichnisNummer.getWahlID())
        || (bezirkIDUndWaehlerverzeichnisNummer.getWaehlerverzeichnisNummer()) == null) {
      throw exceptionOnInvalid;
    }
  }

  public void validStimmabgabevermerkeOrThrow(final StimmabgabevermerkeModel stimmabgabevermerke) {
    if (stimmabgabevermerke == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG);
    }
  }
}
