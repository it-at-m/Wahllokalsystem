package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

public interface WahlenClient {

    WahlartModel getWahlartOfCurrentWahltag(final String wahlID);
}
