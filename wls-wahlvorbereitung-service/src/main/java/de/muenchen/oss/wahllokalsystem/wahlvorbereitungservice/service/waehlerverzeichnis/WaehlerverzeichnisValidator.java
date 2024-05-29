package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.waehlerverzeichnis;

import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaehlerverzeichnisValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWaehlerverzeichnisReferenceOrThrow(final BezirkIDUndWaehlerverzeichnisNummer waehlerverzeichnisReference) {
        if (waehlerverzeichnisReference == null || waehlerverzeichnisReference.getWahlbezirkID() == null || waehlerverzeichnisReference.getWahlbezirkID()
                .isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validModelToSetOrThrow(final WaehlerverzeichnisModel waehlerverzeichnisModelToSet) {
        if (waehlerverzeichnisModelToSet == null || waehlerverzeichnisModelToSet.waehlerverzeichnisReference() == null
                || waehlerverzeichnisModelToSet.waehlerverzeichnisReference()
                        .getWaehlerverzeichnisNummer() == null
                || waehlerverzeichnisModelToSet.waehlerverzeichnisReference()
                        .getWahlbezirkID() == null
                || waehlerverzeichnisModelToSet.waehlerverzeichnisReference().getWahlbezirkID().isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.PARAMS_UNVOLLSTAENDIG);
        }
    }
}
