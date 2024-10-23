package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahllokalZustandValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID, WahllokalZustandOperation zustandOperation) {
        if (StringUtils.isEmpty(wahlbezirkID) || StringUtils.isBlank(wahlbezirkID)) {
            switch (zustandOperation) {
            case POST_LASTSEEN -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG);
            case POST_LETZTEABMELDUNG ->
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG);
            default -> throw exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG));
            }
        }
    }

    public void validWahlIdUndWahlbezirkIDOrThrow(final BezirkUndWahlID bezirkUndWahlID, WahllokalZustandOperation zustandOperation) {
        if (bezirkUndWahlID == null || StringUtils.isEmpty(bezirkUndWahlID.getWahlID()) || StringUtils.isEmpty(bezirkUndWahlID.getWahlbezirkID()) ||
                StringUtils.isBlank(bezirkUndWahlID.getWahlID()) || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())) {
            switch (zustandOperation) {
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT ->
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            case POST_SCHNELLMELDUNG_DRUCKUHRZEIT ->
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT ->
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            case POST_NIEDERSCHRIFT_DRUCKUHRZEIT ->
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            default -> throw exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG));
            }

        }
    }

    public void validSendungsdatenModel(SendungsdatenModel sendungsdatenModel, WahllokalZustandOperation zustandOperation) {
        validWahlIdUndWahlbezirkIDOrThrow(sendungsdatenModel.bezirkUndWahlID(), zustandOperation);
    }

    public void validDruckdatenModel(DruckdatenModel druckdatenModel, WahllokalZustandOperation zustandOperation) {
        validWahlIdUndWahlbezirkIDOrThrow(druckdatenModel.bezirkUndWahlID(), zustandOperation);
    }
}
