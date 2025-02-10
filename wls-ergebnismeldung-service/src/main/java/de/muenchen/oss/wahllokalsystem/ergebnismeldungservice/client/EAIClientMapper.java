package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerte;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.ergebnisse.Ergebnisse;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.AWerteDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnisDTO;

public interface EAIClientMapper {

    AWerteDTO toDTO(AWerte awerte);

    ErgebnisDTO toDTO(Ergebnisse ergebnis);
}
