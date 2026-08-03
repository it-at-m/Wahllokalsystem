package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.stimmzettelerfassung.stimmzettel;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StimmzettelOfTeamDTO(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int stimmzettelkennung,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) boolean isValid,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int invalideVotes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String gueltigkeit,
    List<BeschlussvormerkungDTO> beschlussvormerkungen,
    BeschlussfassungDTO beschlussfassung,
    List<WahlvorschlagDTO> wahlvorschlaege) {}
