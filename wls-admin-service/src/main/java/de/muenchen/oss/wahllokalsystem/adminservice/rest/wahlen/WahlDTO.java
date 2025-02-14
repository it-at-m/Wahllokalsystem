package de.muenchen.oss.wahllokalsystem.adminservice.rest.wahlen;


import jakarta.validation.constraints.NotNull;

public record WahlDTO(@NotNull String wahlID, @NotNull String name,
                      @NotNull Long reihenfolge,
                      @NotNull Long waehlerverzeichnisnummer,
                      @NotNull java.time.LocalDate wahltag,
                      @NotNull WahlartDTO wahlart,
                      FarbeDTO farbe
       ) {
}
