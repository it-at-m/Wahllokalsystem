package de.muenchen.oss.wahllokalsystem.eaiservice.service;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IDConverter {

    private final ExceptionFactory exceptionFactory;

    //CHECKSTYLE.OFF: AbbreviationAsWordInName - illegal match cause UUID should not be shortened
    public UUID convertIDToUUIDOrThrow(final String id) {
        //CHECKSTYLE.ON: AbbreviationAsWordInName
        try {
            return UUID.fromString(id);
        } catch (final IllegalArgumentException illegalArgumentException) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
        }
    }
}
