package de.muenchen.oss.wahllokalsystem.wls.common.exception.builder.states;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface ExceptionInitializable {
    CodeIsSet<? extends WlsException> withCode(String code);
}
