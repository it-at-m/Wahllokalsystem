package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FunktionsamenMappingPropertiesTest {

    @Autowired
    private FunktionsnamenMappingProperties mappingProperties;

    @Test
    void should_notReturnPlaceholderOrEmptyStrings_when_mappedCorrectly() {
        Map<WahlbezirkArtModel, Map<String, Map<String, String>>> mapping = mappingProperties.getMapping();

        mapping.forEach(((wahlbezirkArtModel, wahlartenMap) -> { // wahlbezirkArtModel = UWB, BWB
            wahlartenMap.forEach((wahlart, funktionenMap) -> { // wahlart = BAW, BEB, BTW, BZW, EUW, LTW, MBW, OBW, SRW, SVW, VE
                funktionenMap.forEach((funktion, wert) -> { // funktion = W, SB, SWB, SSB, B
                    assertThat(wert).doesNotContain("${");
                    assertThat(wert).isNotBlank();
                });
            });
        }));
    }
}
