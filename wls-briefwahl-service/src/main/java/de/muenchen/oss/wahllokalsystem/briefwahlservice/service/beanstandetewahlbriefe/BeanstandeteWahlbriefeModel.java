package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;

@Builder
public record BeanstandeteWahlbriefeModel(@NotNull String wahlbezirkID, @NotNull Long waehlerverzeichnisNummer,
                                          @NotNull Map<String, Zurueckweisungsgrund[]> beanstandeteWahlbriefe) {
}
