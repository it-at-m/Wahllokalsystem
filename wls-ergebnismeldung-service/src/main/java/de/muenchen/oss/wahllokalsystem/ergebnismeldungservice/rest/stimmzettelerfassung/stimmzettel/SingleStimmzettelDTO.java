package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

// TODO Single musste verwendete werden weil es bei den Stimmabgabevermerken bereits ein
// StimmzettelDTO gibt und es zur Kollision kommt
public record SingleStimmzettelDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Integer invalideVotes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) StimmzettelGueltigkeitDTO gueltigkeit,
    List<BeschlussgrundDTO> beschlussvorschlag,
    BeschlussfassungDTO beschlussfassung,
    List<WahlvorschlagDTO> wahlvorschlaege) {}
