package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface ExceptionFinalizable<T extends WlsException> {
    T buildWithMessage(String message);
}
