package de.muenchen.oss.wahllokalsystem.authservice.rest;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResolvedUrlDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) String url) {
}
