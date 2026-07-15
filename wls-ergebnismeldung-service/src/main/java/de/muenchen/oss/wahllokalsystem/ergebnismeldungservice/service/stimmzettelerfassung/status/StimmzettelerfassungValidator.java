package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StimmzettelerfassungValidator {

  public void validBezirkUndWahlIdOrThrow(
      final BezirkUndWahlID bezirkUndWahlId, final FachlicheWlsException exceptionOnInvalid)
      throws FachlicheWlsException {
    if (bezirkUndWahlId == null
        || StringUtils.isBlank(bezirkUndWahlId.getWahlID())
        || StringUtils.isBlank(bezirkUndWahlId.getWahlbezirkID())) {
      throw exceptionOnInvalid;
    }
  }
}
