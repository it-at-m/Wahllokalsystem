package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.infomanagementClient;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface KonfigurierterWahltagClient {

    KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException;
}
