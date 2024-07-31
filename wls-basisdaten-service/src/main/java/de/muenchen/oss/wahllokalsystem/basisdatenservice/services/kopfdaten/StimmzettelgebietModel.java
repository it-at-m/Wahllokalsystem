package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StimmzettelgebietModel(String identifikator,
                                     String nummer,
                                     String name,
                                     LocalDate wahltag,
                                     @NotNull StimmzettelgebietsartModel stimmzettelgebietsart) {
}
