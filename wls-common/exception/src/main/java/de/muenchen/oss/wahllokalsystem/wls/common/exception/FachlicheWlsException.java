package de.muenchen.oss.wahllokalsystem.wls.common.exception;

public class FachlicheWlsException extends WlsException {

	public FachlicheWlsException(String code, String service, String message) {
		super(WlsExceptionCategory.FACHLICH, code, service, message);
	}

	public FachlicheWlsException(Throwable cause, String code, String service, String message) {
		super(cause, WlsExceptionCategory.FACHLICH, code, service, message);
	}
}
