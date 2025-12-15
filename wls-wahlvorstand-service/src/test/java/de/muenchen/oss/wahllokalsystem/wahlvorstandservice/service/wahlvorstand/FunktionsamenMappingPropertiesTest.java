package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FunktionsamenMappingPropertiesTest {

  @Autowired private FunktionsnamenMappingProperties mappingProperties;

  @Test
  void should_displayNamesOfFunktionenAndNoPlaceholders_when_mappedCorrectly() {
    Map<WahlbezirkArtModel, Map<String, Map<String, String>>> mapping =
        mappingProperties.getMapping();

    Arrays.stream(WahlbezirkArtModel.values())
        .forEach(
            wahlbezirkArt -> {
              Arrays.stream(WahlartModel.values())
                  .forEach(
                      wahlart -> {
                        Arrays.stream(FunktionModel.values())
                            .forEach(
                                funktion -> {
                                  val mappedValue =
                                      mapping
                                          .get(wahlbezirkArt)
                                          .get(wahlart.name())
                                          .get(funktion.name());
                                  assertThat(mappedValue).doesNotContain("${");
                                  assertThat(mappedValue).isNotBlank();
                                });
                      });
            });
  }
}
