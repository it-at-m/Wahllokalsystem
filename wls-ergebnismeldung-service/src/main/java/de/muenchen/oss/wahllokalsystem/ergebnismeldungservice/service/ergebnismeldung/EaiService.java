package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;

public interface EaiService {

    //TODO: das Interface sollte nicht auf das Datenmodell der EAI zugreifen
    void sendErgebnismeldung(ErgebnismeldungDTO ergebnismeldungDTO);
}
