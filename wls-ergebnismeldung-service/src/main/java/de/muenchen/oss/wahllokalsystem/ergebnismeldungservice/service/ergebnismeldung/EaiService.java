package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;

public interface EaiService {

    void sendErgebnismeldung(ErgebnismeldungDTO ergebnismeldungDTO);
}
