package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopfdatenService {

    private final KopfdatenValidator kopfdatenValidator;
    private final KopfdatenModelMapper kopfdatenModelMapper;
    private final KopfdatenRepository kopfdatenRepository;
    private final WahltagRepository wahltagRepository;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final WahldatenClient wahldatenClient;
    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetKopfdaten')")
    public KopfdatenModel getKopfdaten(BezirkUndWahlID bezirkUndWahlID) {
        log.info("getKopfdaten");

        final Kopfdaten kopfdatenEntity;

        kopfdatenValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);

        val kopfdaten = kopfdatenRepository.findById(bezirkUndWahlID);

        if (kopfdaten.isPresent()) {
            kopfdatenEntity = kopfdaten.get();
        } else {
            log.error("#getKopfdaten: Für Wahlbezirk {} mit WahlID {} waren keine Kopfdaten in der Datenbank", bezirkUndWahlID.getWahlbezirkID(),
                bezirkUndWahlID.getWahlID());

            val konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();

            List<Wahltag> wahltage = wahltagRepository.findAllByOrderByWahltagAsc();
            Wahltag searchedWahltag = wahltage.stream().filter(w -> w.getWahltagID().equals(bezirkUndWahlID.getWahlID())).findAny().orElse(null);
            String wahlNummer = ((null != searchedWahltag) ? searchedWahltag.getNummer() : konfigurierterWahltagModel.nummer());

            BasisdatenModel basisdatenModel = wahldatenClient.loadBasisdaten(searchedWahltag.getWahltag(), wahlNummer);

            kopfdatenEntity = initKopfdata(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID(), basisdatenModel);
        }

        return kopfdatenModelMapper.toModel(kopfdatenEntity);
    }

    private Kopfdaten initKopfdata(String wahlID, String wahlbezirkID, BasisdatenModel basisdaten) {
        BasisstrukturdatenModel basistrukturdaten = basisdaten.basisstrukturdaten().stream()
            .filter(b -> b.wahlID().equals(wahlID) && b.wahlbezirkID().equals(wahlbezirkID))
            .findAny().orElse(null);

        if (basistrukturdaten == null)
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_BASISSTRUKTURDATEN);

        WahlModel wahl = basisdaten.wahlen().stream()
            .filter(w -> w.identifikator().equals(wahlID))
            .findAny().orElse(null);

        WahlbezirkModel wahlbezirk = basisdaten.wahlbezirke().stream()
            .filter(w -> w.identifikator().equals(wahlbezirkID))
            .findAny().orElse(null);

        StimmzettelgebietModel stimmzettelgebiet = basisdaten.stimmzettelgebiete().stream()
            .filter(s -> s.identifikator().equals(basistrukturdaten.stimmzettelgebietID()))
            .findAny().orElse(null);

        if (wahl == null || wahlbezirk == null || stimmzettelgebiet == null)
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_WAHL_WAHLBEZIRK_STIMMZETTELGEBIET);

        return createKopfdaten(wahl, wahlbezirk, stimmzettelgebiet);
    }

    private Kopfdaten createKopfdaten(WahlModel wahl, WahlbezirkModel wahlbezirk, StimmzettelgebietModel stimmzettelgebiet) {

        val bezirkUndWahlID = new BezirkUndWahlID(wahlbezirk.identifikator(), wahl.identifikator());
        val gemeinde = "LHM";

        KopfdatenModel kopfdaten = new KopfdatenModel(
            bezirkUndWahlID,
            gemeinde,
            stimmzettelgebiet.stimmzettelgebietsart(),
            stimmzettelgebiet.nummer(),
            stimmzettelgebiet.name(),
            wahl.name(),
            wahlbezirk.nummer()
        );

        val kopfdatenEntity = kopfdatenModelMapper.toEntity(kopfdaten);
        kopfdatenRepository.save(kopfdatenEntity);

        return kopfdatenEntity;
    }
}
