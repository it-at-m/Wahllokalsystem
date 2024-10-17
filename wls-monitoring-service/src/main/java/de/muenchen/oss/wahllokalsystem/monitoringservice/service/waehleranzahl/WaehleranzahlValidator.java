package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaehleranzahlValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlIdUndWahlbezirkIDOrThrow(final BezirkUndWahlID bezirkUndWahlID) {
        if (bezirkUndWahlID == null || StringUtils.isEmpty(bezirkUndWahlID.getWahlID()) || StringUtils.isEmpty(bezirkUndWahlID.getWahlbezirkID()) ||
                StringUtils.isBlank(bezirkUndWahlID.getWahlID()) || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBETEILIGUNG_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validWaehleranzahlSetModel(WaehleranzahlModel waehleranzahl) {
        validWahlIdUndWahlbezirkIDOrThrow(waehleranzahl.bezirkUndWahlID());
    }
}
