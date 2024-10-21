package de.muenchen.oss.wahllokalsystem.basisdatenservice.clients;

import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart.BTW;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart.EUW;
import static de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart.LTW;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahlart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.StimmzettelgebietsartModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.common.WahltagWithNummer;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.BasisstrukturdatenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.StimmzettelgebietModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten.WahldatenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumoptionModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlageModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen.ReferendumvorlagenReferenceModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenClient;
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
public class DummyClientImpl
        implements WahlvorschlaegeClient, WahltageClient, ReferendumvorlagenClient, WahlenClient, KonfigurierterWahltagClient, WahldatenClient,
        WahlbezirkeClient {

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
    public List<WahlModel> getWahlen(final WahltagWithNummer wahltagWithNummer) throws WlsException {
        return List.of(
                new WahlModel("wahl1", "remoteWahl 0", 1L, 1L, wahltagWithNummer.wahltag(), BTW, new Farbe(0, 1, 2), "1"),
                new WahlModel("wahl2", "remoteWahl 1", 2L, 1L, wahltagWithNummer.wahltag(), EUW, new Farbe(3, 4, 5), "1"),
                new WahlModel("wahl3", "remoteWahl 2", 3L, 1L, wahltagWithNummer.wahltag(), LTW, new Farbe(6, 7, 8), "1"));
    }

    @Override
    public ReferendumvorlagenModel getReferendumvorlagen(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        return new ReferendumvorlagenModel("stimmzettelgebietID", Set.of(new ReferendumvorlageModel("wahlvorschlagID1", 1L, "kurzname1", "frage1",
                Set.of(new ReferendumoptionModel("optionID11" + UUID.randomUUID(), "option11", 1L),
                        new ReferendumoptionModel("optionID12" + UUID.randomUUID(), "option12", 2L))),
                new ReferendumvorlageModel("wahlvorschlagID2", 2L, "kurzname2", "frage2",
                        Set.of(new ReferendumoptionModel("optionID21" + UUID.randomUUID(), "option21", 1L),
                                new ReferendumoptionModel("optionID22" + UUID.randomUUID(), "option22", 2L)))));
    }

    @Override
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {
        return new KonfigurierterWahltagModel(LocalDate.now().plusMonths(1), "wahltagID1", true, "1");
    }

    @Override
    public BasisdatenModel loadBasisdaten(WahltagWithNummer wahltagWithNummer) throws WlsException {
        return new BasisdatenModel(
                Set.of(new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_1", wahltagWithNummer.wahltag()),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID1_2", wahltagWithNummer.wahltag()),
                        new BasisstrukturdatenModel("wahlID1", "szgID", "wahlbezirkID2_1", wahltagWithNummer.wahltag()),
                        new BasisstrukturdatenModel("wahlID2", "szgID", "wahlbezirkID2_2", wahltagWithNummer.wahltag()),
                        new BasisstrukturdatenModel("wahlID2", "szgIDOther", "wahlbezirkID2_2", wahltagWithNummer.wahltag())),
                Set.of(
                        new WahlModel("wahlID1", "Bundestagswahl", 1L, 1L, wahltagWithNummer.wahltag(), Wahlart.BTW, new Farbe(0, 1, 2), "0"),
                        new WahlModel("wahlID2", "Europawahl", 2L, 1L, wahltagWithNummer.wahltag(), Wahlart.EUW, new Farbe(3, 4, 5), "1")),
                Set.of(
                        new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", wahltagWithNummer.wahltag(), "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", wahltagWithNummer.wahltag(), "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", wahltagWithNummer.wahltag(), "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", wahltagWithNummer.wahltag(), "0", "wahlID1"),
                        new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", wahltagWithNummer.wahltag(), "1", "wahlID2")

                ),
                Set.of(
                        new StimmzettelgebietModel("szgID", "120", "Munich", wahltagWithNummer.wahltag(), StimmzettelgebietsartModel.SG),
                        new StimmzettelgebietModel("szgIDOther", "920", "Munich Center", wahltagWithNummer.wahltag(), StimmzettelgebietsartModel.SB)));
    }

    @Override
    public Set<WahlbezirkModel> loadWahlbezirke(LocalDate forDate, String withNummer) throws WlsException {
        return Set.of(
                new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", forDate, "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.BWB, "1251", forDate, "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", forDate, "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_2", WahlbezirkArtModel.BWB, "1252", forDate, "1", "wahlID2"));
    }
}
