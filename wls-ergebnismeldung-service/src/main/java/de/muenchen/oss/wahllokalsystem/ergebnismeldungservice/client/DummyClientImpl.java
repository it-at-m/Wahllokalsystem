package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl
        implements AWerteClient {

    @Override
    public List<AWerteModel> getAWerte(final String wahlbezirkID) throws WlsException {
        if (wahlbezirkID.equals("wahlbezirkID")) {
            return List.of(
                    new AWerteModel(new BezirkUndWahlID("wahlID", "wahlbezirkID"), 25L, 26L));
        } else {
            return List.of();
        }
    }

}
