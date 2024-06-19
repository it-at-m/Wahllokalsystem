package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlaege;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlvorschlaegeValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlIdUndWahlbezirkIDOrThrow(final String wahlID, final String wahlbezirkID) {
        if (wahlID == null || wahlID.isEmpty() || wahlbezirkID == null || wahlbezirkID.isEmpty()) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.SUCHKRITERIEN_UNVOLLSTAENDIG);
        }
    }
}
