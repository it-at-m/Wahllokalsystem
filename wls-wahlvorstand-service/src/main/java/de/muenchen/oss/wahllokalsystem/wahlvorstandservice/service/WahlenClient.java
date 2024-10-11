package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

import java.util.List;

public interface WahlenClient {

    List<WahlModel> getWahlen(final WahltagWithNummer wahltagWithNummer) throws WlsException;
}
