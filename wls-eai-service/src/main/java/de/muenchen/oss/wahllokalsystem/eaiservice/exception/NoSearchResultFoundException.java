package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import lombok.Getter;

@Getter
public class NoSearchResultFoundException extends RuntimeException {

    private final Class<?> notFoundEntityClass;
    private final Object[] searchParameter;

    public NoSearchResultFoundException(Class<?> notFoundEntityClass, Object... searchParameter) {
        this.notFoundEntityClass = notFoundEntityClass;
        this.searchParameter = searchParameter;
    }
}
