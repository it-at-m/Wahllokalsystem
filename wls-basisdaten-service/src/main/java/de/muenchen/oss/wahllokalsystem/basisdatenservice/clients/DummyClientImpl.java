package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.StimmzettelgebietModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
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
public class DummyClientImpl implements WahlvorschlaegeClient, WahltageClient, KonfigurierterWahltagClient, WahldatenClient {

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
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {

        return new KonfigurierterWahltagModel(LocalDate.now().plusMonths(1), "wahltagID1", true, "1");
    }

    @Override
    public BasisdatenModel loadBasisdaten(LocalDate forDate, String withNummer) throws WlsException {

        return new BasisdatenModel(
                Set.of(new BasisstrukturdatenModel("wahlID1","szgID", "wahlbezirkID1_1", forDate),
                        new BasisstrukturdatenModel("wahlID1","szgID", "wahlbezirkID1_2", forDate),
                        new BasisstrukturdatenModel("wahlID1","szgID", "wahlbezirkID2_1", forDate),
                        new BasisstrukturdatenModel("wahlID2","szgID", "wahlbezirkID2_2", forDate),
                        new BasisstrukturdatenModel("wahlID2","szgIDOther", "wahlbezirkID2_2", forDate)
                ),
                Set.of(
                        new WahlModel("wahlID1", "0", 1L, 1L, forDate, Wahlart.BTW, new Farbe(0, 1, 2), "1"),
                        new WahlModel("wahlID2", "1", 2L, 1L, forDate, Wahlart.EUW, new Farbe(3, 4, 5), "1")
                ),
                Set.of(
                        new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID2")

                ),
                Set.of(
                        new StimmzettelgebietModel("szgID", "120", "Munich", forDate, StimmzettelgebietsartModel.SG),
                        new StimmzettelgebietModel("szgIDOther", "920", "Munich", forDate, StimmzettelgebietsartModel.SB)
                )
        );
    }
}
