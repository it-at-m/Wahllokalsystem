package de.muenchen.oss.wahllokalsystem.basisdatenservice.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.TechnischeWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExceptionFactory {

    private final ServiceIDFormatter serviceIDFormatter;

    public FachlicheWlsException createFachlicheWlsException(final ExceptionDataWrapper exceptionDataWrapper) {
        return finalizeWlsException(FachlicheWlsException.withCode(exceptionDataWrapper.code()), exceptionDataWrapper.message());
    }

    public TechnischeWlsException createTechnischeWlsException(final ExceptionDataWrapper exceptionDataWrapper) {
        return finalizeWlsException(TechnischeWlsException.withCode(exceptionDataWrapper.code()), exceptionDataWrapper.message());
    }

    private <T extends WlsException> T finalizeWlsException(final CodeIsSet<T> initializedExceptionBuilder, final String message) {
        return initializedExceptionBuilder
                .inService(serviceIDFormatter.getId())
                .buildWithMessage(message);
    }
}
