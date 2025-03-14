package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahllokalbenutzer;

import jakarta.validation.constraints.NotNull;

public record CsvFileDTO(@NotNull String csv) {
}
