package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlart.BTW;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlart.EUW;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlart.LTW;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.KandidatModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlaegeModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlvorschlag.WahlvorschlagModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl implements WahlvorschlaegeClient, WahltageClient, WahlClient {

    @Override
    public WahlvorschlaegeModel getWahlvorschlaege(BezirkUndWahlID bezirkUndWahlID) {
        return new WahlvorschlaegeModel(bezirkUndWahlID, "stimmzettelgebiedID",
                Set.of(new WahlvorschlagModel(UUID.randomUUID().toString(), 1L, "kurzname1", true,
                        Set.of(new KandidatModel(UUID.randomUUID().toString(), "kandidat11", 1L, true, 1L, true),
                                new KandidatModel(UUID.randomUUID().toString(), "kandidat21", 2L, false, 1L, false))),
                        new WahlvorschlagModel(UUID.randomUUID().toString(), 2L, "kurzname2", true,
                                Set.of(new KandidatModel(UUID.randomUUID().toString(), "kandidat21", 1L, true, 1L, true),
                                        new KandidatModel(UUID.randomUUID().toString(), "kandidat22", 2L, false, 1L, false)))));
    }

    @Override
    public List<WahltagModel> getWahltage(LocalDate tag) {
        return List.of(
                new WahltagModel("wahltagID4", LocalDate.now().plusMonths(2), "Beschreibung Wahltag 4", "3"),
                new WahltagModel("wahltagID1", LocalDate.now().minusMonths(2), "Beschreibung Wahltag 1", "0"),
                new WahltagModel("wahltagID3", LocalDate.now().plusMonths(1), "Beschreibung Wahltag 3", "2"),
                new WahltagModel("wahltagID2", LocalDate.now().minusMonths(1), "Beschreibung Wahltag 2", "1"));
    }

    @Override
    public List<WahlModel> getWahlen(LocalDate wahltag, String nummer) throws WlsException {
        return List.of(
                new WahlModel("wahl1", "0", 1, LocalDate.now().plusMonths(2), BTW, new Farbe(), nummer),
                new WahlModel("wahl2", "1", 2, LocalDate.now().minusMonths(2), EUW, new Farbe(), nummer),
                new WahlModel("wahl3", "2", 3, LocalDate.now().plusMonths(1), LTW, new Farbe(), nummer));
    }
}
