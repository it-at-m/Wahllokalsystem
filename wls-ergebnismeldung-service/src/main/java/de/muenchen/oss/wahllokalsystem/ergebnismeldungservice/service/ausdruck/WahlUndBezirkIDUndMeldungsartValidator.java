package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlUndBezirkIDUndMeldungsartValidator {

    public void validWahlUndBezirkIDUndMeldungsartOrThrow(WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart,
            final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (StringUtils.isBlank(wahlUndBezirkIDUndMeldungsart.getWahlID()) ||
                StringUtils.isBlank(wahlUndBezirkIDUndMeldungsart.getWahlbezirkID()) ||
                wahlUndBezirkIDUndMeldungsart.getMeldungsart() == null) {
            throw exceptionOnInvalid;
        }
    }
}
