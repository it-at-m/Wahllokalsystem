package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties
public class FunktionsnamenMappingProperties {

    private final Map<WahlbezirkArtModel, Map<String, Map<String, String>>> mapping = new EnumMap<>(WahlbezirkArtModel.class);
}
