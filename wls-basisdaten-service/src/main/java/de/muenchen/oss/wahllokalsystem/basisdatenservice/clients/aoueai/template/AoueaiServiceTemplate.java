package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.template;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.domain.Wahlvorschlaege;

public interface AoueaiServiceTemplate {
    Wahlvorschlaege getWahlvorschlaege(String wahlID, String wahlbezirkID);
}
