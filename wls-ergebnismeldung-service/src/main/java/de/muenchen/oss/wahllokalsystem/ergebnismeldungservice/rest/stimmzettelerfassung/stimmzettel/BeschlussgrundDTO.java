package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;

public record BeschlussgrundDTO(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) String text) {}
