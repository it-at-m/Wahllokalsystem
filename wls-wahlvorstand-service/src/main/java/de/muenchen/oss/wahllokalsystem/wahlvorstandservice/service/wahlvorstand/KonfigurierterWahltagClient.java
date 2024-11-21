package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface KonfigurierterWahltagClient {

    KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException;
}
