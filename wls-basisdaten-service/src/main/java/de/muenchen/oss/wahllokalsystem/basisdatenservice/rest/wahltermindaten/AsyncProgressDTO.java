package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahltermindaten;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AsyncProgressDTO(
        LocalDate forWahltag,
        String wahlNummer,
        LocalDateTime lastStartTime,
        LocalDateTime lastFinishTime,
        boolean wahlvorschlaegeLoadingActive,
        int wahlvorschlaegeTotal,
        int wahlvorschlageFinished,
        String wahlvorschlaegeNext,
        boolean referendumLoadingActive,
        int referendumVorlagenTotal,
        int referendumVorlagenFinished,
        String referendumVorlagenNext
) {
}
