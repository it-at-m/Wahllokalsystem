package de.muenchen.oss.wahllokalsystem.wahlvorstandservice.clients;

import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.Farbe;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.FunktionModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.WahlModel;
import de.muenchen.oss.wahllokalsystem.wahlvorstandservice.service.wahlvorstand.Wahlart;
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
        return new WahlvorstandModel("wahlbezirkID", LocalDateTime.now(), List.of(
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
                new WahlModel("wahl1", "remoteWahl 0", 1L, 1L, wahltag.wahltag(), Wahlart.BTW, new Farbe(0, 1, 2), "1"),
                new WahlModel("wahl2", "remoteWahl 1", 2L, 1L, wahltag.wahltag(), Wahlart.EUW, new Farbe(3, 4, 5), "1"),
                new WahlModel("wahl3", "remoteWahl 2", 3L, 1L, wahltag.wahltag(), Wahlart.LTW, new Farbe(6, 7, 8), "1"));
    }

    @Override
    public KonfigurierterWahltagModel getKonfigurierterWahltag() throws WlsException {
        return new KonfigurierterWahltagModel(LocalDate.now().plusMonths(1), "wahltagID1", true, "1");
    }
}
