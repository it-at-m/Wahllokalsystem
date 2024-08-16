package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * So eine Validierung existiert im alten Code nicht. Sinnvoll wäre sie, aber die Basisdaten
 * Response ist sehr groß,
 * und getKopfdaten wird immer bei der Anmeldung aufgerufen. Wenn nichts im Repo vorhanden, dann
 * würde auch diese Validierung laufen
 * was bei der Öffnung der Wahllokale für jedes Wahllokal einmal laufen würde -> große vermutlich
 * Last. Ist noch nicht im Service eingebunden
 */
@Component
@RequiredArgsConstructor
public class BasisdatenValidator {

    private final ExceptionFactory exceptionFactory;

    public void validBasisdatenContentOrThrow(BasisdatenModel basisdatenModel) {
        if (!allPropertiesContainingData(basisdatenModel) ||
                !forEveryBasistrutkturdatenCorespondingAtLeastOneWahlOneWahlbezirkAndOneStimmzettelgebiet(basisdatenModel) ||
                !forEveryWahlCorespondingAtLeastOneBasistrutkturdatenAndOneWahlbezirk(basisdatenModel) ||
                !forEveryWahlbezirkCorespondingAtLeastOneBasistrutkturdatenAndOneWahl(basisdatenModel) ||
                !forEveryStimmzettelgebietCorespondingAtLeastOneBasistrutkturdaten(basisdatenModel)) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.INITIALIZE_KOPFDATEN_BASISDATEN_DATA_INCONSISTENTCY);
        }
    }

    private boolean allPropertiesContainingData(BasisdatenModel basisdatenModel) {
        return !(null == basisdatenModel.basisstrukturdaten()) &&
                !basisdatenModel.basisstrukturdaten().isEmpty() &&
                !(null == basisdatenModel.wahlen()) && !basisdatenModel.wahlen().isEmpty() &&
                !(null == basisdatenModel.wahlbezirke()) && !basisdatenModel.wahlbezirke().isEmpty() &&
                !(null == basisdatenModel.stimmzettelgebiete()) && !basisdatenModel.stimmzettelgebiete().isEmpty();
    }

    private boolean forEveryBasistrutkturdatenCorespondingAtLeastOneWahlOneWahlbezirkAndOneStimmzettelgebiet(BasisdatenModel basisdatenModel) {
        return basisdatenModel.basisstrukturdaten().stream().allMatch(
                (bsd) -> basisdatenModel.wahlen().stream().anyMatch(wahl -> wahl.wahlID().equals(bsd.wahlID())) &&
                        basisdatenModel.wahlbezirke().stream().anyMatch(wbz -> wbz.identifikator().equals(bsd.wahlbezirkID())) &&
                        basisdatenModel.stimmzettelgebiete().stream().anyMatch(szg -> szg.identifikator().equals(bsd.stimmzettelgebietID())));
    }

    private boolean forEveryWahlCorespondingAtLeastOneBasistrutkturdatenAndOneWahlbezirk(BasisdatenModel basisdatenModel) {
        return basisdatenModel.wahlen().stream().allMatch(
                (wahl) -> basisdatenModel.basisstrukturdaten().stream().anyMatch(bsd -> bsd.wahlID().equals(wahl.wahlID())) &&
                        basisdatenModel.wahlbezirke().stream().anyMatch(wbz -> wbz.wahlID().equals(wahl.wahlID())));
    }

    private boolean forEveryWahlbezirkCorespondingAtLeastOneBasistrutkturdatenAndOneWahl(BasisdatenModel basisdatenModel) {
        return basisdatenModel.wahlbezirke().stream().allMatch(
                (wbz) -> basisdatenModel.basisstrukturdaten().stream().anyMatch(bsd -> bsd.wahlbezirkID().equals(wbz.identifikator())) &&
                        basisdatenModel.wahlen().stream().anyMatch(w -> w.wahlID().equals(wbz.wahlID())));
    }

    private boolean forEveryStimmzettelgebietCorespondingAtLeastOneBasistrutkturdaten(BasisdatenModel basisdatenModel) {
        return basisdatenModel.stimmzettelgebiete().stream()
                .allMatch(
                        (szg) -> basisdatenModel.basisstrukturdaten().stream().anyMatch(
                                (bsd) -> bsd.stimmzettelgebietID().equals(szg.identifikator())));
    }
}
