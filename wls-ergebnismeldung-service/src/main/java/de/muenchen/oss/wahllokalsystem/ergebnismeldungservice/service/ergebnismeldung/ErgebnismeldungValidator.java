package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ErgebnismeldungValidator {

    private final ExceptionFactory exceptionFactory;

    public void validBezirkUndWahlIDOrThrow(final BezirkUndWahlID bezirkUndWahlID) {
        if (bezirkUndWahlID == null || StringUtils.isBlank(bezirkUndWahlID.getWahlID()) || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SENDERGEBNISSE_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
