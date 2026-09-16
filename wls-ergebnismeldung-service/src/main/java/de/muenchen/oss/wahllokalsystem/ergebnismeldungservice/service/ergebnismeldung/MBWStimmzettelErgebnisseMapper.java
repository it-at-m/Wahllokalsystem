package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel.StimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MBWStimmzettelErgebnisseMapper implements MBWStapelErgebnisCollector {

    private final StimmzettelService stimmzettelService;

    @Override
    @Transactional(readOnly = true)
    public MBWErgebnisseModel getErgebnisse(String wahlID, String wahlbezirkID) {
        val stapelA =  stimmzettelService.getStimmzettelWithExactlyOneWahlvorschlagSelected(new BezirkUndWahlID(wahlID, wahlbezirkID));
        return null;
    }
}
