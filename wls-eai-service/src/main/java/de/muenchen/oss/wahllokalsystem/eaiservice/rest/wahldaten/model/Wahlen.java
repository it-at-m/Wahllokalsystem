package de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record Wahlen(@NotNull @Size(min = 1) Set<Wahl> wahlen) {
}
