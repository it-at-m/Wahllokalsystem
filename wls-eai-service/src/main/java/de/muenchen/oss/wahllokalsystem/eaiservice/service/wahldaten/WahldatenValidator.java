package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahldatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validGetWahltageParameterOrThrow(final LocalDate wahltag) {
        if (wahltag == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLTAGE_TAG_FEHLT);
        }
    }

    public void validGetWahlenParameterOrThrow(final LocalDate wahltag, final String nummer) {
        if (wahltag == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEN_WAHLTAG_FEHLT);
        }

        if (nummer == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEN_NUMMER_FEHLT);
        }
    }

    public void validGetWahlbezirkeParameterOrThrow(final LocalDate wahltag, final String nummer) {
        if (wahltag == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEBZIRKE_WAHLTAG_FEHLT);
        }

        if (nummer == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLEBZIRKE_NUMMER_FEHLT);
        }
    }

    public void validGetWahlberechtigteParameterOrThrow(final String wahlbezirkID) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLBERECHTIGTE_SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }

    public void validGetBasisdatenParameterOrThrow(final LocalDate wahltag, final String nummer) {
        if (wahltag == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADBASISDATEN_TAG_FEHLT);
        }

        if (nummer == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADBASISDATEN_NUMMER_FEHLT);
        }
    }

}
