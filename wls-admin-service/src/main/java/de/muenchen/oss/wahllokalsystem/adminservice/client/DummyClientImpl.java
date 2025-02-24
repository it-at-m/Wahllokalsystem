package de.muenchen.oss.wahllokalsystem.adminservice.client;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.AWerteClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltageClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.WahltermindatenClient;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl implements AWerteClient, KonfigurierterWahltagClient, WahlbezirkeClient, WahltageClient, WahltermindatenClient {

    @Override
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        log.info("dummy client initialiseAWerte({}) called instead of ergebnismeldung-service", wahlbezirkIDs);
    }

    @Override
    public void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag) {
        log.info("dummy client postKonfigurierterWahltag({}) called instead of infomanagement-service", konfigurierterWahltag);
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
}
