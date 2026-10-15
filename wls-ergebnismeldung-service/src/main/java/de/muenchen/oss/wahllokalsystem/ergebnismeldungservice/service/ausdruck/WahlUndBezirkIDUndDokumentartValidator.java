package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndDokumentart;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlUndBezirkIDUndDokumentartValidator {

  public void validWahlUndBezirkIDUndDokumentartOrThrow(
      final WahlUndBezirkIDUndDokumentart wahlUndBezirkIDUndDokumentart,
      final FachlicheWlsException exceptionOnInvalid)
      throws FachlicheWlsException {
    if (wahlUndBezirkIDUndDokumentart == null
        || StringUtils.isBlank(wahlUndBezirkIDUndDokumentart.getWahlID())
        || StringUtils.isBlank(wahlUndBezirkIDUndDokumentart.getWahlbezirkID())
        || wahlUndBezirkIDUndDokumentart.getDokumentart() == null) {
      throw exceptionOnInvalid;
    }
  }
}
