package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping;

import java.util.HashMap;
import java.util.Map;

public class BWBFunktionsnamenMapping {
    private final Map<String, Map<String, String>> bwbFunktion = new HashMap<>();

    public Map<String, Map<String, String>> getBwbFunktion() {
        return bwbFunktion;
    }
}
