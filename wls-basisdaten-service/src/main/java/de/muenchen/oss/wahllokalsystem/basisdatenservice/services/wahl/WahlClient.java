package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.List;

public interface WahlClient {

    List<WahlModel> getWahlen(java.time.LocalDate wahltag, String nummer) throws WlsException;

}
