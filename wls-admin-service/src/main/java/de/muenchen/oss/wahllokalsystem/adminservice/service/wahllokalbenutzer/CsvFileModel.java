package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import jakarta.validation.constraints.NotNull;

public record CsvFileModel(@NotNull String csv) {
}
