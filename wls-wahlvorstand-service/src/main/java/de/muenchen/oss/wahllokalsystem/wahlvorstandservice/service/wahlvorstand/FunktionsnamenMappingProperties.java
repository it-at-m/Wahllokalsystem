package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties
public class FunktionsnamenMappingProperties {

    private final Map<String, Map<String, String>> bwbFunktion = new HashMap<>();

    private final Map<String, Map<String, String>> uwbFunktion = new HashMap<>();

}
