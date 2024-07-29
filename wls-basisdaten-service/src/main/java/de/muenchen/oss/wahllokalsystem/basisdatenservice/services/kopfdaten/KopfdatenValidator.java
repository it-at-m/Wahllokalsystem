package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KopfdatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlIdUndWahlbezirkIDOrThrow(final BezirkUndWahlID bezirkUndWahlID) {
        if (bezirkUndWahlID == null || StringUtils.isEmpty(bezirkUndWahlID.getWahlID()) || StringUtils.isEmpty(bezirkUndWahlID.getWahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
