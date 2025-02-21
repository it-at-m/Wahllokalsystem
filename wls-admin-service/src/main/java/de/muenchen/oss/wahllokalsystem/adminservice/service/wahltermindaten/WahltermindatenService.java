package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltageClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahltermindatenService {

    private final ExceptionFactory exceptionFactory;

    private final WahltermindatenValidator wahltermindatenValidator;

    private final WahltermindatenClient wahltermindatenClient;

    private final WahltageClient wahltageClient;

    private final WahlbezirkeClient wahlbezirkeClient;

    private final AWerteClient aWerteClient;

    private final KonfigurierterWahltagClient konfigurierterWahltagClient;

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public void loadWahltermindaten(final String wahltagID) {
        wahltermindatenValidator.validWahltagIDParamOrThrow(wahltagID);

        log.debug("Initialisiere Wahltermindaten für Wahltag {}", wahltagID);

        // Wahltermindaten herunterladen, Wahlvorschläge herunterladen (async), Referendumvorlagen herunterladen (async)
        wahltermindatenClient.putWahltermindaten(wahltagID);
        val wahltage = wahltageClient.getWahltage();

        log.debug("Habe {} wahltage für {} gefunden", wahltage.size(), wahltagID);

        val first = wahltage.stream().filter(wahltag -> wahltag.wahltagID().equals(wahltagID)).findFirst();

        // A-Werte herunterladen (async)
        try {
            aWerteClient.initialiseAWerte(
                    wahlbezirkeClient.getWahlbezirke(wahltagID).stream()
                            .filter(wahlbezirk -> wahlbezirk.wahlbezirkart().equals(WahlbezirkArtModel.UWB))
                            .map(WahlbezirkModel::wahlbezirkID)
                            .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Asynchrones Laden der A-Werte ist fehlgeschlagen :(");
        }

        if (first.isEmpty()) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.INVALID_ARGUMENT);
        }

        try {
            konfigurierterWahltagClient
                    .postKonfigurierterWahltag(new KonfigurierterWahltagModel(first.get().wahltag(), wahltagID, false, first.get().nummer()));
        } catch (Exception e) {
            log.error("Konnte konfigurierten Wahltag in Infomanagement nicht anlegen. Revert in basisdaten wird ausgeführt...", e);
            // "Rollback..." |-->
            wahltermindatenClient.deleteWahltermindaten(wahltagID);
            // "Rollback..." <--|
            throw e;
        }
    }
}
