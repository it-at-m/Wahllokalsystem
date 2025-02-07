package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;

public interface ElectionTypeValidation {

    boolean supportsWahlart(WahlartModel wahlart);

    boolean isValidUwb(String wahlbezirkID, String wahlID, Long waehlerverzeichnisNummer, MeldungsartModel meldungsart) throws WlsException;

    boolean isValidBwb(String wahlbezirkID, String wahlID, Long waehlerverzeichnisNummer, MeldungsartModel meldungsart) throws WlsException;

}
