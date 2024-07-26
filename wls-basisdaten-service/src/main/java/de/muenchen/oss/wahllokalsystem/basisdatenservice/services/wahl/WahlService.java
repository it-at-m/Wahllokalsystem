package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahl;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlService {

    private final WahlRepository wahlRepository;

    private final WahltagRepository wahltagRepository;

    private final ExceptionFactory exceptionFactory;

    private final WahlModelMapper wahlModelMapper;

    private final WahlClient wahlClient;

    public java.util.List<Wahl> getWahlen(String wahltagID) {
        log.debug("#getWahlen wahltagID > {}", wahltagID);

        val wahlFromRepo = wahlRepository.findById(wahltagID);
        if (wahlFromRepo.isEmpty()) {
            log.debug("#getWahl: Für WahltagID {} war keine Wahl in der Datenbank", wahltagID);
        }
            Optional <Wahltag> wahltag = wahltagRepository.findById(wahltagID);
            if (wahltag.isEmpty()) {
                throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.NULL_FROM_CLIENT);
        }

            if (wahlRepository.countByWahltag(wahltag.get().getWahltag()) == 0) {
                log.error("#getWahlen: Für wahltagID {} waren keine Wahlen in der Datenbank", wahltagID);
                List<Wahl> wahlen = wahlModelMapper.toEntity(wahlClient.getWahlen(wahltag.get().getWahltag(), wahltag.get().getNummer()));
                wahlRepository.saveAll(wahlen);


                wahlen.sort(Comparator.comparing(Wahl::getReihenfolge));
                return wahlen;
            }

        return wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag());
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostWahl')")
    public void setWahl(final WahlModel wahlModel) {
        log.info("postWahl - wahlModel> {}", wahlModel);
        val entityToSave = wahlModelMapper.toEntity(wahlModel);
        try {
            wahlRepository.save(entityToSave);
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.WAHL_SPEICHERN_FEHLGESCHLAGEN);
        }
    }

}
