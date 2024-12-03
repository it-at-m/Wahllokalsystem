package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StatusValidator {

    private final ExceptionFactory exceptionFactory;

    public void valideBezirkUndWahlIdOrThrow(final BezirkUndWahlID bezirkUndWahlId, final FachlicheWlsException exceptionToThrow)
            throws FachlicheWlsException {
        if (bezirkUndWahlId == null || StringUtils.isBlank(bezirkUndWahlId.getWahlID()) || StringUtils.isBlank(bezirkUndWahlId.getWahlbezirkID())) {
            throw exceptionToThrow;
        }
    }

    public void valideStatusOrThrow(final StatusModel status) {
        if (status == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
