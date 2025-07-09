package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.common;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahltagWithNummerModel(@NotNull LocalDate wahltag,@NotNull String wahltagNummer){}
