package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping;

import java.util.HashMap;
import java.util.Map;

public class UWBFunktionsnamenMapping {
    private final Map<String, Map<String, String>> uwbFunktion = new HashMap<>();

    public Map<String, Map<String, String>> getUwbFunktion() {
        return uwbFunktion;
    }
}
