package de.muenchen.oss.wahllokalsystem.monitoringservice.service.wahllokalzustand;

import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WahllokalZustandValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(String wahlbezirkID, WahllokalZustandOperation zustandOperation) {
        if (null == wahlbezirkID || StringUtils.isEmpty(wahlbezirkID) || StringUtils.isBlank(wahlbezirkID)) {
            switch (zustandOperation) {
            case POST_LASTSEEN -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LASTSEEN_SUCHKRITERIEN_UNVOLLSTAENDIG);
            case POST_LETZTEABMELDUNG -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_LETZTEABMELDUNG_SUCHKRITERIEN_UNVOLLSTAENDIG);
            default -> throw exceptionFactory.createFachlicheWlsException((ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG));
            }
        }
    }

    public void validWahlIdUndWahlbezirkIDOrThrow(final BezirkUndWahlID bezirkUndWahlID, WahllokalZustandOperation zustandOperation) throws WlsException {
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
        switch (zustandOperation) {
            case POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT -> {
                if (null == sendungsdatenModel) {
                    throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
                } else validWahlIdUndWahlbezirkIDOrThrow(sendungsdatenModel.bezirkUndWahlID(), zustandOperation);
            }
            case POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT -> {
                if (null == sendungsdatenModel) {
                    throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_SENDUNGSUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
                } else validWahlIdUndWahlbezirkIDOrThrow(sendungsdatenModel.bezirkUndWahlID(), zustandOperation);
            }
            default -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validDruckdatenModel(DruckdatenModel druckdatenModel, WahllokalZustandOperation zustandOperation) throws WlsException {
        switch (zustandOperation) {
        case POST_SCHNELLMELDUNG_DRUCKUHRZEIT -> {
            if (null == druckdatenModel) {
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_SCHNELLMELDUNG_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            } else validWahlIdUndWahlbezirkIDOrThrow(druckdatenModel.bezirkUndWahlID(), zustandOperation);
        }
        case POST_NIEDERSCHRIFT_DRUCKUHRZEIT -> {
            if (null == druckdatenModel) {
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_NIEDERSCHRIFT_DRUCKUHRZEIT_SUCHKRITERIEN_UNVOLLSTAENDIG);
            } else validWahlIdUndWahlbezirkIDOrThrow(druckdatenModel.bezirkUndWahlID(), zustandOperation);
        }
        default -> throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.DEFAULT_WAHLLOKALZUSTAND_EXCEPTION_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }
}
