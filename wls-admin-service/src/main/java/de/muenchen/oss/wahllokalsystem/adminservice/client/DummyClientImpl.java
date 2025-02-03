package de.muenchen.oss.wahllokalsystem.adminservice.client;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.service.AWerteClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahlbezirkeClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltageClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltermindatenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
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

    }

    @Override
    public void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltag) {

    }

    @Override
    public List<WahlbezirkModel> getWahlbezirke(String wahltagID) {
        return List.of(
                new WahlbezirkModel("wahlbezirkID1_1", WahlbezirkArtModel.UWB, "1201", LocalDate.now(), "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID1_2", WahlbezirkArtModel.UWB, "1251", LocalDate.now(), "0", "wahlID1"),
                new WahlbezirkModel("wahlbezirkID2_1", WahlbezirkArtModel.UWB, "1202", LocalDate.now(), "0", "wahlID1"));
    }

    @Override
    public List<WahltagModel> getWahltage() {
        return List.of(
                new WahltagModel("wahltagID1", LocalDate.now().minusMonths(2), "Beschreibung Wahltag 1", "0"),
                new WahltagModel("wahltagID3", LocalDate.now().plusMonths(1), "Beschreibung Wahltag 3", "2"),
                new WahltagModel("wahltagID2", LocalDate.now().minusMonths(1), "Beschreibung Wahltag 2", "1"));
    }

    @Override
    public void putWahltermindaten(String wahltagID) throws WlsException {

    }

    @Override
    public void deleteWahltermindaten(String wahltagID) throws WlsException {

    }
}
