package de.muenchen.oss.wahllokalsystem.briefwahlservice.service.beanstandetewahlbriefe;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BeanstandeteWahlbriefeReference(@NotNull String wahlbezirkID, @NotNull Long waehlerverzeichnisNummer) {
}
