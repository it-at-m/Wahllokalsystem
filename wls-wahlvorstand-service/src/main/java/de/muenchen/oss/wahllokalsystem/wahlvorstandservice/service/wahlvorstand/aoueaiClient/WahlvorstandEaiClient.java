package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.aoueaiClient;

import java.time.LocalDate;

public interface WahlvorstandEaiClient {

    WahlvorstandModel getWahlvorstand(String wahlbezirkID, LocalDate wahltag);

    void postWahlvorstand(WahlvorstandModel wahlvorstand);
}
