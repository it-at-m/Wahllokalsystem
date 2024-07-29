package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopfdatenService {

    private final KopfdatenValidator kopfdatenValidator;
    private final KopfdatenRepository kopfdatenRepository;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final ExceptionFactory exceptionFactory;
    private final WahltagRepository wahltagRepository;
    private final KopfdatenModelMapper kopfdatenModelMapper;

    public KopfdatenModel getKopfdaten(BezirkUndWahlID bezirkUndWahlID) {
        log.info("getKopfdaten");

        // 1. validiere wahlID und wahlbezirkID
        kopfdatenValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);

        // 2. hole Kopfdaten aus Kopfdaten-Repo
        val kopfdaten = kopfdatenRepository.findById(bezirkUndWahlID);
        // wenn nicht vorhanden dann
        if (kopfdaten.isEmpty()) {
            // loggge Fehler
            log.error("#getKopfdaten: Für Wahlbezirk {} mit WahlID {} waren keine Kopfdaten in der Datenbank", bezirkUndWahlID.getWahlbezirkID(),
                bezirkUndWahlID.getWahlID());
            // wahltag: hole konfigurierten Wahltag von infomanagement-service
            val konfigurierterWahltagDTO = konfigurierterWahltagClient.getKonfigurierterWahltag();
            // wenn kein Wahltag gefunden dann
            // werfe CODE_GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG WlsException
            if (konfigurierterWahltagDTO == null)
                throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG);
            // hole alle Wahlen aus Wahl-Repo für den konfigurierten Wahltag
            List<Wahltag> wahltage = wahltagRepository.findAllByOrderByWahltagAsc();
            // finde in der Liste an Wahlen den Wahltag mit der passenden wahlID
            Wahltag searchedWahltag = wahltage.stream().filter(w -> w.getWahltagID().equals(bezirkUndWahlID.getWahlID())).findAny().orElse(null);
            // wahlNummer: wenn ein passender wahltag mit wahlId gefunden dann hole dessen Nummer,
            // ansonsten nimm die Nummer des konfigurierten Wahltages
            String wahlNummer = ((null != searchedWahltag) ? searchedWahltag.getNummer() : konfigurierterWahltagDTO.getNummer());

            //          - basisdaten: hole vom eai-service die Basisdaten für den wahltag und die wahlNummer
            //              - wenn nicht vorhanden werfe CODE_GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG WlsException

            //          - initiiere Kopfdaten aus wahlID, wahlBezirkId und basisdaten
            //              - speichere Kopfdaten in Kopfdaten-Repo
        }
        /// alt ///

        //
        //        if (kopfdaten == null) {
        //            LOGGER.error("#getKopfdaten: Für Wahlbezirk {} mit WahlID {} waren keine Kopfdaten in der Datenbank", wahlbezirkID, wahlID);
        //
        //            KonfigurierterWahltag_ wahltag = infomanagementServiceTemplate.getKonfigurierterWahltag();
        //
        //            if (wahltag == null)
        //                throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_GETKOPFDATEN_NO_KONFIGURIERTERWAHLTAG);
        //
        //            List<Wahl_> wahlen = wahlRepository.findByWahltagOrderByReihenfolge(wahltag.getWahltag());
        //            Wahl_ searchedWahl = wahlen.stream().filter(w -> w.getWahlID().equals(wahlID)).findAny().orElse(null);
        //            String wahlNummer = ((null != searchedWahl) ? searchedWahl.getNummer() : wahltag.getNummer());
        //
        //            Basisdaten_ basisdaten = aoueaiTemplate.getBasisdaten(wahltag.getWahltag(), wahlNummer);
        //
        //            if (basisdaten == null)
        //                throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_GETKOPFDATEN_NO_BASISDATEN);
        //            kopfdaten = initKopfdata(wahlID, wahlbezirkID, basisdaten);
        //        }
        //
        //        /// end alt ///
        //        // TODO: Implementierung
        //
        //        Optional<Kopfdaten> kopfdatenFromRepo = kopfdatenRepository.findById(bezirkUndWahlID);
        //        return kopfdatenModelMapper.toModel(kopfdatenFromRepo.get());
        //    }
        //
        //    // alt
        //    protected Kopfdaten_ initKopfdata(String wahlID, String wahlbezirkID, Basisdaten_ basisdaten) {
        //        Basisstrukturdaten_ basistrukturdaten = basisdaten.getBasisstrukturdaten().stream()
        //            .filter(b -> b.getWahlID().equals(wahlID) && b.getWahlbezirkID().equals(wahlbezirkID))
        //            .findAny().orElse(null);
        //
        //        if (basistrukturdaten == null)
        //            throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_INITIALIZE_KOPFDATEN_NO_BASISSTRUKTURDATEN);
        //
        //        Wahl_ wahl = Mapping.toEntity(basisdaten.getWahlen().stream()
        //            .filter(w -> w.getIdentifikator().equals(wahlID))
        //            .findAny().orElse(null));
        //
        //        Wahlbezirk_ wahlbezirk = Mapping.toEntity(basisdaten.getWahlbezirke().stream()
        //            .filter(w -> w.getIdentifikator().equals(wahlbezirkID))
        //            .findAny().orElse(null));
        //
        //        Stimmzettelgebiet_ stimmzettelgebiet = basisdaten.getStimmzettelgebiete().stream()
        //            .filter(s -> s.getIdentifikator().equals(basistrukturdaten.getStimmzettelgebietID()))
        //            .findAny().orElse(null);
        //
        //        if (wahl == null || wahlbezirk == null || stimmzettelgebiet == null)
        //            throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_INITIALIZE_KOPFDATEN_NO_WAHL_WAHLBEZIRK_STIMMZETTELGEBIET);
        //
        //        return createKopfdaten(wahl, wahlbezirk, stimmzettelgebiet);
        //    }
        //
        //    private Kopfdaten_ createKopfdaten(Wahl_ wahl, Wahlbezirk_ wahlbezirk, Stimmzettelgebiet_ stimmzettelgebiet) {
        //        Kopfdaten_ kopfdaten = new Kopfdaten_();
        //        kopfdaten.setGemeinde("LHM");
        //        kopfdaten.setStimmzettelgebietsart(Stimmzettelgebietsart_.valueOf(stimmzettelgebiet.getStimmzettelgebietart().name()));
        //        kopfdaten.setStimmzettelgebietsname(stimmzettelgebiet.getName());
        //        kopfdaten.setStimmzettelgebietsnummer(stimmzettelgebiet.getNummer());
        //        kopfdaten.setBezirkUndWahlID(new BezirkUndWahlID(wahlbezirk.getWahlbezirkID(), wahl.getWahlID()));
        //        kopfdaten.setWahlname(wahl.getName());
        //        kopfdaten.setWahlbezirknummer(wahlbezirk.getNummer());
        //
        //        kopfdatenRepository.save(kopfdaten);
        return kopfdatenModelMapper.toModel(kopfdaten.get());
    }
    // end alt
}
