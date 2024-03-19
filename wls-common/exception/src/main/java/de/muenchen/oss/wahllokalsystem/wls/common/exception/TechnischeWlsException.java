package de.muenchen.oss.wahllokalsystem.wls.common.exception;

public class TechnischeWlsException extends WlsException {

	public TechnischeWlsException(String code, String service, String message) {
		this(null, code, service, message);
	}

	public TechnischeWlsException(Throwable cause, String code, String service, String message) {
		super(cause, WlsExceptionCategory.TECHNISCH, code, service, message);
	}
}
