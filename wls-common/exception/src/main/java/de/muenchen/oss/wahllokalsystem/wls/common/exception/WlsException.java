package de.muenchen.oss.wahllokalsystem.wls.common.exception;

import lombok.Getter;

@Getter
public abstract class WlsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final WlsExceptionCategory category;
    private final String code;
    //Kann aus Konstruktoren raus da die Exception primär intern erstellt wird
    //wird nur gesetzt wenn eine Exception von einen anderen Service verabeitet wird
    private final String service;
    private final String message;

    public WlsException(final WlsExceptionCategory category, final String code, final String service, final String message) {
        this(null, category, code, service, message);
    }

    public WlsException(final Throwable cause, final WlsExceptionCategory category, final String code, final String service, final String message) {
        super(cause);
        this.category = category;
        this.code = code;
        this.service = service;
        this.message = message;
    }
}
