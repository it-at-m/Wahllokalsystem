package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;

@Builder
public record BeanstandeteWahlbriefeCreateDTO(@NotNull Map<String, Zurueckweisungsgrund[]> beanstandeteWahlbriefe) {
}
