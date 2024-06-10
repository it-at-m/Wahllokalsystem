package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest;

import java.util.Optional;
import org.springframework.http.ResponseEntity;

public abstract class AbstractController {

    protected <T> ResponseEntity<T> withBodyOrNoContent(final Optional<T> body) {
        return body.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }
}
