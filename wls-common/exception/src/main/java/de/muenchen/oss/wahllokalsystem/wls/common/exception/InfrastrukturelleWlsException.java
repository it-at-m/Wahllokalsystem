package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionCreator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.WlsExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

public final class InfrastrukturelleWlsException extends WlsException {

    private static final WlsExceptionCreator<InfrastrukturelleWlsException> exceptionCreation = (InfrastrukturelleWlsException::new);

    private InfrastrukturelleWlsException(final WlsExceptionData data) {
        super(WlsExceptionCategory.INFRASTRUKTUR, data);
    }

    public static CodeIsSet<InfrastrukturelleWlsException> withCode(final String code) {
        return new WlsExceptionFactory<>(exceptionCreation).withCode(code);
    }
}
