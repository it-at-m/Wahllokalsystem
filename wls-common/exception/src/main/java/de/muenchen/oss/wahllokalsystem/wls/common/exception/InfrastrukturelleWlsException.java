package de.muenchen.oss.wahllokalsystem.wls.common.exception;

public class InfrastrukturelleWlsException extends WlsException {

    public InfrastrukturelleWlsException(String code, String service, String message) {
        this(null, code, service, message);
    }

    public InfrastrukturelleWlsException(Throwable cause, String code, String service, String message) {
        super(cause, WlsExceptionCategory.INFRASTRUKTUR, code, service, message);
    }
}
