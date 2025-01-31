package de.muenchen.oss.wahllokalsystem.adminservice.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahltermindatenService {

    private final WahltermindatenValidator wahltermindatenValidator;

    private final WahltermindatenClient wahltermindatenClient;

    private final WahltageClient wahltageClient;

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public void loadWahltermindaten(String wahltagID) {
        wahltermindatenValidator.validWahltagIDParamOrThrow(wahltagID);

        log.debug("Initialisiere Wahltermindaten für Wahltag {}", wahltagID);

        // Wahltermindaten herunterladen, Wahlvorschläge herunterladen (async), Referendumvorlagen herunterladen (async)
        wahltermindatenClient.putWahltermindaten(wahltagID);
        List<WahltagModel> wahltage = wahltageClient.getWahltage();

        log.debug("Habe {} wahltage für gefunden", wahltage.size());

        Optional<WahltagModel> first = wahltage.stream().filter(wahltag -> wahltag.wahltagID().equals(wahltagID)).findFirst();
//
//        // A-Werte herunterladen (async)
//        try {
//            ergebnismeldungTemplate.initialiseAWerte(
//                basisdatenTemplate.getWahlbezirke(wahltagID).stream()
//                    .filter(wahlbezirk_ -> wahlbezirk_.getWahlbezirkart().equals(Wahlbezirksart_.UWB))
//                    .map(Wahlbezirk_::getWahlbezirkID)
//                    .collect(Collectors.toList())
//            );
//        } catch (Exception e) {
//            log.error("Asynchrones Laden der A-Werte ist fehlgeschlagen :(");
//        }
//
//        if (!first.isPresent()) {
//            throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_INVALID_ARGUMENT, "Kein Wahltag vorhanden mit Wahltag-ID " + wahltagID);
//        }
//
//        try {
//            infomanagementTemplate.postKonfigurierterWahltag(new KonfigurierterWahltag_(first.get().getWahltag(), wahltagID, WahltagStatus_.INAKTIV, first.get().getNummer()));
//        } catch (Exception e) {
//            LOG.error("Konnte konfigurierten Wahltag in Infomanagement nicht anlegen. Revert in basisdaten wird ausgeführt...", e);
//            // "Rollback..." |-->
//            basisdatenTemplate.deleteWahltermindaten(wahltagID);
//            // "Rollback..." <--|
//            throw e;
//        }

    }
}
