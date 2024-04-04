package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.CodeIsSet;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.ExceptionFinalizable;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states.ExceptionInitializable;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

public class WlsExceptionFactory<T extends WlsException> implements ExceptionFinalizable<T>, CodeIsSet<T>, ExceptionInitializable {
    private final WlsExceptionData wlsExceptionData;

    private final WlsExceptionCreator<T> exceptionCreator;

    public WlsExceptionFactory(final WlsExceptionCreator<T> builder) {
        wlsExceptionData = new WlsExceptionData();
        this.exceptionCreator = builder;
    }

    @Override
    public CodeIsSet<T> withCode(final String code) {
        wlsExceptionData.setCode(code);
        return this;
    }

    @Override
    public CodeIsSet<T> inService(final String serviceName) {
        wlsExceptionData.setServiceName(serviceName);
        return this;
    }

    @Override
    public CodeIsSet<T> withCause(final Throwable cause) {
        wlsExceptionData.setCause(cause);
        return this;
    }

    @Override
    public T buildWithMessage(final String message) {
        wlsExceptionData.setMessage(message);
        return exceptionCreator.createWlsException(wlsExceptionData);
    }
}
