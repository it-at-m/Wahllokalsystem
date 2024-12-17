package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.rest.awerte;

import java.time.LocalDateTime;

public record AsyncProgressDTO(
        LocalDateTime lastStartTime,
        LocalDateTime lastFinishTime,
        boolean aWerteLoadingActive,
        int aWerteTotal,
        int aWerteFinished,
        String aWerteNext
) {
}
