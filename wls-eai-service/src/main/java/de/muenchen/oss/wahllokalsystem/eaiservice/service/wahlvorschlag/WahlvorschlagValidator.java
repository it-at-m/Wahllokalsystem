package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WahlvorschlagValidator {

    private final ExceptionFactory exceptionFactory;

    public void validateWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_BEZIRKID_FEHLT);
        }
    }

    public void validateWahlIDOrThrow(final String wahlID) {
        if (StringUtils.isBlank(wahlID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.LOADWAHLVORSCHLAEGE_WAHLID_FEHLT);
        }
    }
}
