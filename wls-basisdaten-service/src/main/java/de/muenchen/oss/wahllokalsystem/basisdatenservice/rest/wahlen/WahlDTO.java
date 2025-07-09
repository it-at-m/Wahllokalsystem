package de.muenchen.oss.wahllokalsystem.basisdatenservice.rest.wahlen;

import jakarta.validation.constraints.NotNull;

public record WahlDTO(@NotNull String wahlID,@NotNull String name,@NotNull Long reihenfolge,@NotNull Long waehlerverzeichnisNummer,@NotNull java.time.LocalDate wahltag,@NotNull WahlartDTO wahlart,@NotNull FarbeDTO farbe,String nummer){}
