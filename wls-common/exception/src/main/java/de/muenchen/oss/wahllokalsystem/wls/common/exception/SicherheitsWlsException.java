package de.muenchen.oss.wahllokalsystem.wls.common.exception;

public class SicherheitsWlsException extends WlsException {

	public SicherheitsWlsException(String code, String service, String message) {
		this(null, code, service, message);
	}

	public SicherheitsWlsException(Throwable cause, String code, String service, String message) {
		super(cause, WlsExceptionCategory.SECURITY, code, service, message);
	}
}
