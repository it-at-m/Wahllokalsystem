package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.client;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.eai.aou.model.ErgebnismeldungDTO;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte.AWerteModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.BriefwahlClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.EaiService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.UrnenwahlClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlenClient;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.status.sender.StatusClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(Profiles.DUMMY_CLIENTS)
@Slf4j
public class DummyClientImpl
        implements AWerteClient, StatusClient, UrnenwahlClient, WahlenClient, BriefwahlClient, EaiService {

    @Override
    public List<AWerteModel> getAWerte(final String wahlbezirkID) throws WlsException {
        return List.of(
                new AWerteModel(new BezirkUndWahlID("wahlID01", wahlbezirkID), 25L, 26L));
    }

    @Override
    public void postSchnellmeldungSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungSendungsuhrzeit)
            throws WlsException {
        log.info("dummy client postSchnellmeldungSendungsuhrzeit of {} on {} called instead of monitoring", bezirkUndWahlID, schnellmeldungSendungsuhrzeit);
    }

    @Override
    public void postSchnellmeldungDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime schnellmeldungDruckuhrzeit) throws WlsException {
        log.info("dummy client postSchnellmeldungDruckuhrzeit of {} on {} called instead of monitoring", bezirkUndWahlID, schnellmeldungDruckuhrzeit);
    }

    @Override
    public void postNiederschriftSendungsuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftSendungsuhrzeit) throws WlsException {
        log.info("dummy client postNiederschriftSendungsuhrzeit of {} on {} called instead of monitoring", bezirkUndWahlID, niederschriftSendungsuhrzeit);
    }

    @Override
    public void postNiederschriftDruckuhrzeit(BezirkUndWahlID bezirkUndWahlID, LocalDateTime niederschriftDruckuhrzeit) throws WlsException {
        log.info("dummy client postNiederschriftDruckuhrzeit of {} on {} called instead of monitoring", bezirkUndWahlID, niederschriftDruckuhrzeit);
    }

    @Override
    public boolean isGeschlossen(String wahlbezirkID) {
        return true;
    }

    @Override
    public WahlartModel getWahlartOfCurrentWahltag(String wahlID) {
        return WahlartModel.BTW;
    }

    @Override
    public long getAnzahlZurueckgewiesenerWahlbriefe(String wahlbezirkID, String wahlID, long waehlerverzeichnisNummer) {
        return 0;
    }

    @Override
    public void sendErgebnismeldung(ErgebnismeldungDTO ergebnismeldungDTO) {
        log.info("dummy client sendErgebnismeldung {}", ergebnismeldungDTO);
    }
}
