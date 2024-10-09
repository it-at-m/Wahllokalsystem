package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.domain.Wahlvorstand;

import java.time.LocalDate;

public interface EaiClient {

    Wahlvorstand getWahlvorstand(String wahlbezirkID, LocalDate wahltag);

    void postWahlvorstand(Wahlvorstand wahlvorstand, LocalDate wahltag);
}
