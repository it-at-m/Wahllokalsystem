package de.muenchen.oss.wahllokalsystem.eaiservice.exception;

import java.util.UUID;
import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private final UUID requestedID;

    private final Class<?> entityClass;

    public NotFoundException(UUID requestID, Class<?> entityClass) {
        this.requestedID = requestID;
        this.entityClass = entityClass;
    }
}
