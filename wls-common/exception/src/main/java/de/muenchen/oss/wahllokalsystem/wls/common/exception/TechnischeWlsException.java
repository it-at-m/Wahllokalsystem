package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionCreator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

public final class TechnischeWlsException extends WlsException {

    private static final WlsExceptionCreator<TechnischeWlsException> exceptionCreation = (TechnischeWlsException::new);

    private TechnischeWlsException(final WlsExceptionData data) {
        super(WlsExceptionCategory.TECHNISCH, data);
    }

    public static CodeIsSet<TechnischeWlsException> withCode(final String code) {
        return new WlsExceptionFactory<>(exceptionCreation).withCode(code);
    }
}
