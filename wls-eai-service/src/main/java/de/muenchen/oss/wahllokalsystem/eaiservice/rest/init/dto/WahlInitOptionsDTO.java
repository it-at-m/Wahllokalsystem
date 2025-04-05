package de.muenchen.oss.wahllokalsystem.eaiservice.rest.init.dto;

import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.dto.WahlartDTO;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record WahlInitOptionsDTO(@NotNull WahlartDTO wahlart,
                                 @NotNull String name,
                                 RandomStimmzettelgebieteInitOptionsDTO randomStimmzettelgebiete,
                                 List<StimmzettelgebietInitOptionsDTO> stimmzettelgebietInitOptions) {
}
