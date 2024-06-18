package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.template.impl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.domain.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.clients.aoueai.template.AoueaiServiceTemplate;
import org.springframework.stereotype.Service;

@Service
public class AoueaiServiceTemplateImpl implements AoueaiServiceTemplate {
    @Override
    public Wahlvorschlaege getWahlvorschlaege(String wahlID, String wahlbezirkID) {
        return null;
    }
}
