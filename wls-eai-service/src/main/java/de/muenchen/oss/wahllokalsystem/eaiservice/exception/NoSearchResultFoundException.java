package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import lombok.Getter;

@Getter
public class NoSearchResultFoundException extends RuntimeException {

    private final Class<?> notFound;
    private final Object[] searchParameter;

    public NoSearchResultFoundException(Class<?> notFound, Object... searchParameter) {
        this.notFound = notFound;
        this.searchParameter = searchParameter;
    }
}
