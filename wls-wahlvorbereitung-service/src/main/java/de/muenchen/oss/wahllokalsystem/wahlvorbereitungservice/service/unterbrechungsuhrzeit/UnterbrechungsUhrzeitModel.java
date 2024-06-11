package de.muenchen.oss.wahllokalsystem.wahlvorbereitungservice.service.unterbrechungsuhrzeit;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UnterbrechungsUhrzeitModel(String wahlbezirkID, LocalDateTime unterbrechungsUhrzeit) {

}
