package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlenService {

    private final WahlRepository wahlRepository;

    private final WahltagRepository wahltagRepository;

    private final ExceptionFactory exceptionFactory;

    private final WahlModelMapper wahlModelMapper;

    private final WahlenClient wahlenClient;

    private final WahlenValidator wahlenValidator;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetWahlen')")
    @Transactional
    public List<WahlModel> getWahlen(String wahltagID) {
        wahlenValidator.validWahlenCriteriaOrThrow(wahltagID);
        val wahltag = wahltagRepository.findById(wahltagID);

        wahlenValidator.validateWahltagForSearchingWahltagID(wahltag); //TODO sollte Teil des Services werden

        if (wahlRepository.countByWahltag(wahltag.get().getWahltag()) == 0) {
            log.info("#getWahlen: Für wahltagID {} waren keine Wahlen in der Datenbank", wahltagID);
            List<Wahl> wahlEntities = wahlModelMapper
                    .fromListOfWahlModeltoListOfWahlEntities(wahlenClient.getWahlen(wahltag.get().getWahltag(), wahltag.get().getNummer()));
            wahlRepository.saveAll(wahlEntities);
        }
        return wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag()));
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostWahlen')")
    @Transactional
    public void postWahlen(final WahlenWriteModel wahlenWriteModel) {
        log.info("#postWahlen");
        wahlenValidator.validWahlenWriteModelOrThrow(wahlenWriteModel);
        try {
            wahlRepository.saveAll(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(wahlenWriteModel.wahlen()));
        } catch (Exception e) {
            log.error("#postWahlen: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden {}:", e);
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_POSTWAHLEN_UNSAVEABLE);
        }
    }

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_ResetWahlen')"
    )
    @Transactional
    public void resetWahlen() {
        log.info("#resetWahlen");
        try {
            val existingWahlenToReset = wahlRepository.findAll();
            existingWahlenToReset.forEach(this::resetWahl);
            wahlRepository.saveAll(existingWahlenToReset);
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.RESET_WAHLEN_NICHT_ERFOLGREICH);
        }
    }

    private void resetWahl(final Wahl wahl) {
        wahl.setFarbe(new Farbe(0, 0, 0));
        wahl.setReihenfolge(0);
        wahl.setWaehlerverzeichnisnummer(1);
    }
}
