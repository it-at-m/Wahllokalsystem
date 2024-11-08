package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.mapping;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class FunktionsnamenMapping {

    private final Map<String, Map<String, String>> bwbFunktion = new HashMap<>();

    private final Map<String, Map<String, String>> uwbFunktion = new HashMap<>();

}
