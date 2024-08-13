package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.kopfdaten;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.kopfdaten.KopfdatenRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
public class KopfdatenService {

    private final KopfdatenValidator kopfdatenValidator;
    private final KonfigurierterWahltagClient konfigurierterWahltagClient;
    private final WahldatenClient wahldatenClient;
    private final KopfdatenRepository kopfdatenRepository;
    private final KopfdatenModelMapper kopfdatenModelMapper;
    private final ExceptionFactory exceptionFactory;




    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetKopfdaten')")
    @Transactional
    public KopfdatenModel getKopfdaten(BezirkUndWahlID bezirkUndWahlID) {
        log.info("getKopfdaten");

        final KopfdatenModel kopfdatenModel;

        kopfdatenValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);
        final InitializeKopfdaten kopfDataInitializer = new InitializeKopfdaten(exceptionFactory);


        val kopfdaten = kopfdatenRepository.findById(bezirkUndWahlID);

        if (kopfdaten.isPresent()) {
            kopfdatenModel = kopfdatenModelMapper.toModel(kopfdaten.get());
        } else {
            log.error("#getKopfdaten: Für Wahlbezirk {} mit WahlID {} waren keine Kopfdaten in der Datenbank", bezirkUndWahlID.getWahlbezirkID(),
                    bezirkUndWahlID.getWahlID());
            KonfigurierterWahltagModel konfigurierterWahltagModel = konfigurierterWahltagClient.getKonfigurierterWahltag();
            BasisdatenModel basisdatenModel = wahldatenClient.loadBasisdaten(konfigurierterWahltagModel.wahltag(), konfigurierterWahltagModel.nummer());
            kopfdatenModel = kopfDataInitializer.initKopfdata(bezirkUndWahlID.getWahlID(), bezirkUndWahlID.getWahlbezirkID(), basisdatenModel);
            kopfdatenRepository.save(kopfdatenModelMapper.toEntity(kopfdatenModel));
        }
        return kopfdatenModel;
    }
}
