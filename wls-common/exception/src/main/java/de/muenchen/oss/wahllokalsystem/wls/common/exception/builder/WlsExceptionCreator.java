package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;

@FunctionalInterface
public interface WlsExceptionCreator<T extends WlsException> {
    T createWlsException(WlsExceptionData wlsExceptionData);
}
