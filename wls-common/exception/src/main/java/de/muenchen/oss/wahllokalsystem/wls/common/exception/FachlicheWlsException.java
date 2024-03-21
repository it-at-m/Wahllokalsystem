package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionCreator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

public final class FachlicheWlsException extends WlsException {

    private static final WlsExceptionCreator<FachlicheWlsException> exceptionCreation = (FachlicheWlsException::new);

    private FachlicheWlsException(final WlsExceptionData data) {
        super(WlsExceptionCategory.FACHLICH, data);
    }

    public static CodeIsSet<FachlicheWlsException> withCode(final String code) {
        return new WlsExceptionFactory<>(exceptionCreation).withCode(code);
    }
}
