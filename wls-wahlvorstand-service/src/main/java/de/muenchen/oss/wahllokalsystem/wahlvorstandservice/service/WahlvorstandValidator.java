package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WahlvorstandValidator {

    private final ExceptionFactory exceptionFactory;

    public void validWahlbezirkIDOrThrow(final String wahlbezirkID) {
        if (StringUtils.isBlank(wahlbezirkID)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLVORSTAND_PARAMETER_UNVOLLSTAENDIG);
        }
    }

    public void validWahlvorstandAndWahlbezirkIDOrThrow(final WahlvorstandModel wahlvorstand) {
        if (wahlvorstand == null || StringUtils.isBlank(wahlvorstand.wahlbezirkID())) {
            log.warn("#postEreignis: Parameter unvollständig");
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.POSTWAHLVORSTAND_PARAMETER_UNVOLLSTAENDIG);
        }
    }
}
