package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke.models.StimmabgabevermerkeModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkIDUndWaehlerverzeichnisNummer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class StimmabgabevermerkeValidator {

    private final ExceptionFactory exceptionFactory;

    public void validBezirkIDUndWaehlerverzeichnisnummerOrThrow(final BezirkIDUndWaehlerverzeichnisNummer bezirkIDUndWaehlerverzeichnisNummer, final FachlicheWlsException exceptionOnInvalid)
            throws FachlicheWlsException {
        if (bezirkIDUndWaehlerverzeichnisNummer == null || StringUtils.isBlank(bezirkIDUndWaehlerverzeichnisNummer.getWahlbezirkID()) || (null == bezirkIDUndWaehlerverzeichnisNummer.getWaehlerverzeichnisNummer()) || (bezirkIDUndWaehlerverzeichnisNummer.getWaehlerverzeichnisNummer() < 0)) {
            throw exceptionOnInvalid;
        }
    }

    public void validStimmabgabevermerkeOrThrow(final StimmabgabevermerkeModel stimmabgabevermerke) {
        if (stimmabgabevermerke == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POST_STATUS_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
