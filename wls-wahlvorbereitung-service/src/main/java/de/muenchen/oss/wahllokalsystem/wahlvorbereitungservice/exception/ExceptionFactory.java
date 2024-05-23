package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExceptionFactory {

    private final ServiceIDFormatter serviceIDFormatter;

    public FachlicheWlsException createFachlicheWlsException(final ExceptionDataWrapper exceptionDataWrapper) {
        return FachlicheWlsException.withCode(exceptionDataWrapper.code()).inService(serviceIDFormatter.getId())
                .buildWithMessage(exceptionDataWrapper.message());
    }

    public TechnischeWlsException createTechnischeWlsException(final ExceptionDataWrapper exceptionDataWrapper) {
        return TechnischeWlsException.withCode(exceptionDataWrapper.code()).inService(serviceIDFormatter.getId())
                .buildWithMessage(exceptionDataWrapper.message());
    }
}
