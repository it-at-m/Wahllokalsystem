package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionCreator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

public final class SicherheitsWlsException extends WlsException {

    private static final WlsExceptionCreator<SicherheitsWlsException> exceptionCreation = (SicherheitsWlsException::new);

    private SicherheitsWlsException(final WlsExceptionData data) {
        super(WlsExceptionCategory.SECURITY, data);
    }

    public static CodeIsSet<SicherheitsWlsException> withCode(final String code) {
        return new WlsExceptionFactory<>(exceptionCreation).withCode(code);
    }
}
