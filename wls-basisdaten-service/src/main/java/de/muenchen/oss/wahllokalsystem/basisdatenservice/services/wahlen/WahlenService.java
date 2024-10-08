package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlen.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
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

    private final WahltageService wahltageService;

    private final ExceptionFactory exceptionFactory;

    private final WahlModelMapper wahlModelMapper;

    private final WahlenClient wahlenClient;

    private final WahlenValidator wahlenValidator;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetWahlen')")
    @Transactional
    public List<WahlModel> getWahlen(String wahltagID) {
        wahlenValidator.validWahlenCriteriaOrThrow(wahltagID);

        val wahltagValue = wahltageService.getWahltagByID(wahltagID);

        if (!wahlRepository.existsByWahltag(wahltagValue.wahltag())) {
            log.info("#getWahlen: Für wahltagID {} waren keine Wahlen in der Datenbank", wahltagID);
            List<Wahl> wahlEntities = wahlModelMapper
                    .fromListOfWahlModeltoListOfWahlEntities(
                            wahlenClient.getWahlen(new WahltagWithNummer(wahltagValue.wahltag(), wahltagValue.nummer())));
            wahlRepository.saveAll(wahlEntities);
        }
        return wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlRepository.findByWahltagOrderByReihenfolge(wahltagValue.wahltag()));
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetWahlen')")
    public List<WahlModel> getExistingWahlenOrderedByReihenfolge(final String wahltagID) {
        val wahltagValue = wahltageService.getWahltagByID(wahltagID);
        return wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlRepository.findByWahltagOrderByReihenfolge(wahltagValue.wahltag()));
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostWahlen')")
    @Transactional
    public void postWahlen(final WahlenWriteModel wahlenWriteModel) {
        log.info("#postWahlen");
        wahlenValidator.validWahlenWriteModelOrThrow(wahlenWriteModel);
        try {
            wahlRepository.saveAll(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(wahlenWriteModel.wahlen()));
        } catch (Exception e) {
            log.error("#postWahlen: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden:", e);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTWAHLEN_UNSAVEABLE);
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
