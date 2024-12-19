package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.wahlscheine;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlscheineValidator {

    private final ExceptionFactory exceptionFactory;

    public void validBezirkUndWahlIdOrThrow(final BezirkUndWahlID bezirkUndWahlId, final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (bezirkUndWahlId == null || StringUtils.isBlank(bezirkUndWahlId.getWahlID()) || StringUtils.isBlank(bezirkUndWahlId.getWahlbezirkID())) {
            throw exceptionOnInvalid;
        }
    }

    public void validWahlscheineOrThrow(final WahlscheineModel wahlscheineModel) {
        if (wahlscheineModel == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_WAHLSCHEINE_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
