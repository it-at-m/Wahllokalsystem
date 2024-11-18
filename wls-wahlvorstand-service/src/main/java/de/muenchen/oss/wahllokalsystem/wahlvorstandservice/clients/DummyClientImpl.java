package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.FunktionModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlartModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandEaiClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlvorstandsmitgliedModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile(Profiles.DUMMY_CLIENTS)
public class DummyClientImpl implements WahlvorstandEaiClient, WahlenClient, KonfigurierterWahltagClient {
    @Override
    public WahlvorstandModel getWahlvorstand(String wahlbezirkID, LocalDate wahltag) {
        return new WahlvorstandModel(wahlbezirkID, LocalDateTime.now(), List.of(
                new WahlvorstandsmitgliedModel("id1", "müller", "max", FunktionModel.B, "Beisitzer*in", true),
                new WahlvorstandsmitgliedModel("id2", "meier", "max", FunktionModel.W, "Wahlvorsteher*in", true),
                new WahlvorstandsmitgliedModel("id3", "mustermann", "max", FunktionModel.SWB, "Stellvertretung Wahlvorsteher*in", true)));
    }

    @Override
    public void postWahlvorstand(WahlvorstandModel wahlvorstand) {
        log.info(wahlvorstand.toString());
    }

    @Override
    public List<WahlModel> getWahlen(KonfigurierterWahltagModel wahltag) throws WlsException {
        return List.of(
                new WahlModel(1L, WahlartModel.BTW),
                new WahlModel(2L, WahlartModel.EUW),
                new WahlModel(3L, WahlartModel.LTW));
    }

    @Override
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {
        return new KonfigurierterWahltagModel(LocalDate.now().plusMonths(1), "1");
    }
}
