package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import java.util.Map;

public record MBWErgebnisseModel(
        Map<String, Integer> stapelA,
        Map<String, Integer> stapelB,
        Map<String, Integer> stapelDUngueltig,
        Map<String, Integer> stapelEUngueltig,
        Map<String, Map<String, Integer>> stimmenJeKandidatStapelBC
) {
}
