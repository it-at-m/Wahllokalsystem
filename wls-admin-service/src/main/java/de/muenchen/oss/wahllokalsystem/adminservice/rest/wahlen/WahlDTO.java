package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;


import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record WahlDTO(@NotNull String wahlID,
                      @NotNull String name,
                      @NotNull Long reihenfolge,
                      @NotNull Long waehlerverzeichnisnummer,
                      @NotNull LocalDate wahltag,
                      @NotNull WahlartDTO wahlart,
                      FarbeDTO farbe) {
}
