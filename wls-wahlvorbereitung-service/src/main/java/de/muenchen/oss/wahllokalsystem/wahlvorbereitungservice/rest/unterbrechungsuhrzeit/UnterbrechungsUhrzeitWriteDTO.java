package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.rest.unterbrechungsuhrzeit;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UnterbrechungsUhrzeitWriteDTO(LocalDateTime unterbrechungsUhrzeit) {

}
