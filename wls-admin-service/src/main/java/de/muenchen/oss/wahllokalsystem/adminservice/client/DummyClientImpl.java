package de.muenchen.oss.wahllokalsystem.adminservice.client;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlenClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltageClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.FarbeModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlartModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.AWerteClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl implements AWerteClient, KonfigurierterWahltagClient, WahlbezirkeClient, WahltageClient, WahltermindatenClient, WahlenClient {

    @Override
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        log.info("dummy client initialiseAWerte({}) called instead of ergebnismeldung-service", wahlbezirkIDs);
    }

    @Override
    public void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag) {
        log.info("dummy client postKonfigurierterWahltag({}) called instead of infomanagement-service", konfigurierterWahltag);
    }

    @Override
    public void deleteKonfigurierterWahltag(String wahltagID) {
        log.info("dummy client deleteKonfigurierterWahltag({}) called instead of infomanagement-service", wahltagID);
    }

    @Override
    public List<WahlbezirkModel> getWahlbezirke(String wahltagID) {
        log.info("dummy client getWahlbezirke({}) called instead of basisdaten-service", wahltagID);
        return List.of(
                new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", LocalDate.now(), "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.UWB, "1251", LocalDate.now(), "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", LocalDate.now(), "0", "wahlID1"));
    }

    @Override
    public List<WahltagModel> getWahltage() {
        log.info("dummy client getWahltage() called instead of infomanagement-service");
        return List.of(
                new WahltagModel("wahltagID", LocalDate.now().minusMonths(2), "Beschreibung Wahltag 1", "0"),
                new WahltagModel("wahltagID", LocalDate.now().plusMonths(1), "Beschreibung Wahltag 3", "2"),
                new WahltagModel("wahltagID", LocalDate.now().minusMonths(1), "Beschreibung Wahltag 2", "1"));
    }

    @Override
    public void putWahltermindaten(String wahltagID) {
        log.info("dummy client putWahltermindaten({}) called instead of basisdaten-service", wahltagID);
    }

    @Override
    public void deleteWahltermindaten(String wahltagID) {
        log.info("dummy client deleteWahltermindaten({}) called instead of basisdaten-service", wahltagID);
    }

    @Override
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() {
        log.info("dummy client getKonfigurierteWahltage() called instead of infomanagement-service");
        return List.of(
                new KonfigurierterWahltagModel(LocalDate.now(), "wahltagID1", true, "0"),
                new KonfigurierterWahltagModel(LocalDate.now().minusMonths(1), "wahltagID2", false, "1"),
                new KonfigurierterWahltagModel(LocalDate.now().plusMonths(2), "wahltagID3", true, "2"));
    }

    @Override
    public void resetWahlen() {
        log.info("dummy client resetWahlen() called instead of basisdaten-service");
    }

    @Override
    public List<WahlModel> getWahlen(String wahltagID) throws WlsException {
        log.info("dummy client getWahlen() called instead of basisdaten-service");
        return List.of(
                new WahlModel(wahltagID, "name" + "wahl1", 1L,
                        1L, LocalDate.now().plusMonths(1),
                        WahlartModel.BAW, new FarbeModel(1, 1, 1)),
                new WahlModel(wahltagID, "name" + "wahl2", 2L,
                        2L, LocalDate.now().plusMonths(2),
                        WahlartModel.LTW, new FarbeModel(2, 2, 2)),
                new WahlModel(wahltagID, "name" + "wahl3", 3L,
                        3L, LocalDate.now().plusMonths(3),
                        WahlartModel.LTW, new FarbeModel(3, 3, 3)));
    }

    @Override
    public void postWahlen(String wahltagID, List<WahlModel> wahlen) {
        log.info("dummy client postWahlen() called instead of basisdaten-service");
    }
}
