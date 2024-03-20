package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionCategory;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.model.WlsExceptionData;
import lombok.Getter;

@Getter
//@formatter:off steht im Conflict mit checkstyle - TODO Issue erstellen und verlinken
public abstract sealed class WlsException extends RuntimeException
        permits FachlicheWlsException, TechnischeWlsException, InfrastrukturelleWlsException, SicherheitsWlsException {
    //formatter:on

    private static final long serialVersionUID = 1L;

    private final WlsExceptionCategory category;
    private final String code;
    private final String serviceName;
    private final String message;

    protected WlsException(final WlsExceptionCategory category, WlsExceptionData wlsExceptionData) {
        super(wlsExceptionData.getCause());
        this.category = category;
        this.code = wlsExceptionData.getCode();
        this.serviceName = wlsExceptionData.getServiceName();
        this.message = wlsExceptionData.getMessage();
    }

}
