package de.muenchen.oss.wahllokalsystem.monitoringservice.service.waehleranzahl;

import de.muenchen.oss.wahllokalsystem.monitoringservice.domain.wahleranzahl.WahleranzahlRepository;
import de.muenchen.oss.wahllokalsystem.monitoringservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WaehleranzahlService {

    private final WaehleranzahlValidator waehleranzahlValidator;
    private final WahleranzahlRepository waehleranzahlRepository;
    private final WaehleranzahlModelMapper waehleranzahlModelMapper;
    private final ExceptionFactory exceptionFactory;

    public WaehleranzahlModel getWahlbeteiligung(BezirkUndWahlID bezirkUndWahlID) {

        waehleranzahlValidator.validWahlIdUndWahlbezirkIDOrThrow(bezirkUndWahlID);

        return waehleranzahlModelMapper.toModel(waehleranzahlRepository.findFirstByBezirkUndWahlIDOrderByUhrzeitDesc(bezirkUndWahlID));
    }

    /**
     * This BusinessAction's purpose is: Speichern und Weiterleiten der Wahlbeteiligung.
     */
    public void postWahlbeteiligung(WaehleranzahlModel waehleranzahl) {

        waehleranzahlValidator.validWaehleranzahlSetModel(waehleranzahl);

        try {
            waehleranzahlRepository.save(waehleranzahlModelMapper.toEntity(waehleranzahl));
        } catch (Exception e) {
            log.error("#postWahlbeteiligung: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTWAHLBETEILIGUNG_UNSAVEABLE);
        }

        //
        //        waehleranzahl.setBezirkUndWahlID(new BezirkUndWahlID(waehleranzahl.getBezirkUndWahlID().getWahlbezirkID(), null));
        //        aoueaiTemplate.saveWahlbeteiligung(getWahlbeteiligungsMeldung_(waehleranzahl));
    }
}
