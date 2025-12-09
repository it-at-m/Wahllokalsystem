package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlen;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

@Builder public record WahlModel(@NotNull String wahlID,@NotNull String name,@NotNull Long reihenfolge,@NotNull Long waehlerverzeichnisNummer,@NotNull LocalDate wahltag,@NotNull WahlartModel wahlart,@NotNull FarbeModel farbe,String nummer){

}
