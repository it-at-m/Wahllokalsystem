package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import java.util.Map;

public record MBWErgebnisseModel(
        Map<String, Long> stapelA,
        Map<String, Long> stapelB,
        long stapelDUngueltig,
        long stapelEUngueltig,
        Map<String, Map<String, Long>> stimmenJeKandidatStapelBC
) {
}
