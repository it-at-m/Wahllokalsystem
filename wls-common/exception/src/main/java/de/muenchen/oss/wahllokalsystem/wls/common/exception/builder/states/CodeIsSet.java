package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface CodeIsSet<T extends WlsException> extends ExceptionFinalizable<T> {
    CodeIsSet<T> inService(String serviceName);

    CodeIsSet<T> withCause(Throwable cause);
}
