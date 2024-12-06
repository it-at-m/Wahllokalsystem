package de.muenchen.oss.wahllokalsystem.briefwahlservice.rest.beanstandetewahlbriefe;

import de.muenchen.oss.wahllokalsystem.briefwahlservice.common.beanstandetewahlbriefe.Zurueckweisungsgrund;
import de.muenchen.oss.wahllokalsystem.briefwahlservice.configuration.SwaggerConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.Builder;

@Builder
public record BeanstandeteWahlbriefeDTO(@NotNull String wahlbezirkID, @NotNull Long waehlerverzeichnisNummer,
                                        @Schema(enumAsRef = true, example = SwaggerConfiguration.BEANSTANDETE_WAHLBRIEFE_EXAMPLE)
                                        @NotNull Map<String, Zurueckweisungsgrund[]> beanstandeteWahlbriefe) {
}
