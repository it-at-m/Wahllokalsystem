package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ausdruck.WahlUndBezirkIDUndMeldungsart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlUndBezirkIDUndMeldungsartValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlUndBezirkIDUndMeldungsartOrThrow(WahlUndBezirkIDUndMeldungsart wahlUndBezirkIDUndMeldungsart) {
        if (StringUtils.isBlank(wahlUndBezirkIDUndMeldungsart.getWahlID()) ||
                StringUtils.isBlank(wahlUndBezirkIDUndMeldungsart.getWahlbezirkID()) ||
                wahlUndBezirkIDUndMeldungsart.getMeldungsart() == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GET_AUSDRUCK_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
