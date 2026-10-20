package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record SingleStimmzettelDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Integer invalideVotes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) StimmzettelGueltigkeitDTO gueltigkeit,
    List<WahlvorstandBeschlussgrundDTO> wahlvorstandBeschlussvorschlag,
    List<SystemBeschlussgrundDTO> systemBeschlussvorschlag,
    BeschlussfassungDTO beschlussfassung,
    List<WahlvorschlagDTO> wahlvorschlaege) {}
