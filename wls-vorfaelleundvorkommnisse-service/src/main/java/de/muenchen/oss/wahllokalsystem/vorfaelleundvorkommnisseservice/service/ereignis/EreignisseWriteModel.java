package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record EreignisseWriteModel(@NotNull String wahlbezirkID,
                                   List<EreignisModel> ereigniseintraege) {
}
