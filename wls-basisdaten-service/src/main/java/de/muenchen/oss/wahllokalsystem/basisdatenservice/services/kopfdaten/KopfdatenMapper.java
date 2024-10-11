package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KopfdatenMapper {

    private final ExceptionFactory exceptionFactory;

    public List<KopfdatenModel> initKopfdaten(BasisdatenModel basisdatenModel) {
        return basisdatenModel.basisstrukturdaten()
                .stream().map(basisstrukturdaten -> initKopfdata(basisstrukturdaten.wahlID(), basisstrukturdaten.wahlbezirkID(), basisdatenModel))
                .toList();
    }

    protected KopfdatenModel initKopfdata(String wahlID, String wahlbezirkID, BasisdatenModel basisdaten) {
        BasisstrukturdatenModel basisstrukturdaten = basisdaten.basisstrukturdaten().stream()
                .filter(b -> b.wahlID().equals(wahlID) && b.wahlbezirkID().equals(wahlbezirkID))
                .findAny().orElse(null);

        if (basisstrukturdaten == null)
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_BASISSTRUKTURDATEN);

        WahlModel wahl = basisdaten.wahlen().stream()
                .filter(w -> w.wahlID().equals(wahlID))
                .findAny().orElse(null);

        WahlbezirkModel wahlbezirk = basisdaten.wahlbezirke().stream()
                .filter(w -> w.wahlbezirkID().equals(wahlbezirkID) && w.wahlID().equals(wahlID))
                .findAny().orElse(null);

        StimmzettelgebietModel stimmzettelgebiet = basisdaten.stimmzettelgebiete().stream()
                .filter(s -> s.identifikator().equals(basisstrukturdaten.stimmzettelgebietID()))
                .findAny().orElse(null);

        if (wahl == null || wahlbezirk == null || stimmzettelgebiet == null)
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_NO_WAHL_WAHLBEZIRK_STIMMZETTELGEBIET);

        return createKopfdaten(wahl, wahlbezirk, stimmzettelgebiet);
    }

    private KopfdatenModel createKopfdaten(WahlModel wahl, WahlbezirkModel wahlbezirk, StimmzettelgebietModel stimmzettelgebiet) {
        val bezirkUndWahlID = new BezirkUndWahlID(wahl.wahlID(), wahlbezirk.wahlbezirkID());
        val gemeinde = "LHM";

        return new KopfdatenModel(
                bezirkUndWahlID,
                gemeinde,
                stimmzettelgebiet.stimmzettelgebietsart(),
                stimmzettelgebiet.nummer(),
                stimmzettelgebiet.name(),
                wahl.name(),
                wahlbezirk.nummer());
    }
}
