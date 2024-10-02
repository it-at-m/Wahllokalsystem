package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;

@Builder
public record WahlbezirkEreignisseModel(@NotNull String wahlbezirkID,
                                        boolean keineVorfaelle,
                                        boolean keineVorkommnisse,
                                        List<EreignisModel> ereigniseintraege) {
}
