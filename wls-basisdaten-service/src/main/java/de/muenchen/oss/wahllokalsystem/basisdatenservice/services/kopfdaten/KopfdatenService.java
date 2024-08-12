package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.Kopfdaten;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KopfdatenService extends InitializeKopfdaten {

    private final KopfdatenValidator kopfdatenValidator;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final WahldatenClient wahldatenClient;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetKopfdaten')")
    @Transactional
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
            KonfigurierterWahltagModel konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
            BasisdatenModel basisdatenModel = wahldatenClient.loadBasisdaten(konfigurierterWahltagModel.wahltag(), konfigurierterWahltagModel.nummer());
            kopfdatenEntity = initKopfdata(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID(), basisdatenModel);
        }

        return kopfdatenModelMapper.toModel(kopfdatenEntity);
    }
}
