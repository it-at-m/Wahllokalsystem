package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface WahlenClient {

    List<WahlModel> getWahlen(final KonfigurierterWahltagModel wahltag) throws WlsException;
}
