package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Farbe;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahlart;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
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
public class WahlService {

    private final WahlRepository wahlRepository;

    private final WahltagRepository wahltagRepository;

    private final ExceptionFactory exceptionFactory;

    private final WahlModelMapper wahlModelMapper;

    private final WahlenClient wahlenClient;

    private final WahlenValidator wahlenValidator;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetWahlen')")
    @Transactional(readOnly = true)
    public List<WahlModel> getWahlen(String wahltagID) {
        wahlenValidator.validWahltagIDParamOrThrow(wahltagID);
        val wahltag = wahltagRepository.findById(wahltagID);
        wahlenValidator.validateWahltagForSearchingWahltagID(wahltag);

        if(wahlRepository.countByWahltag(wahltag.get().getWahltag()) == 0){
            log.error("#getWahlen: Für wahltagID {} waren keine Wahlen in der Datenbank", wahltagID);
            List<Wahl> wahlEntities =  wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(wahlenClient.getWahlen(wahltag.get().getWahltag(), wahltag.get().getNummer()));
            wahlRepository.saveAll(wahlEntities);
        }
        return wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag()));
    }

    public void postWahlen(String wahltagID, List<WahlModel> wahlen) {
        log.info("#postWahlen");

//        if (Strings.isNullOrEmpty(wahltagID) || wahlen == null || wahlen.isEmpty()) {
//            throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_POSTWAHLEN_PARAMETER_UNVOLLSTAENDIG);
//        }
//
//        try {
//            wahlRepository.save(wahlen);
//        } catch (Exception e) {
//            LOGGER.error("#postWahlen: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden {}", e);
//            throw WlsExceptionFactory.build(ExceptionKonstanten.CODE_POSTWAHLEN_UNSAVEABLE);
//        }
    }

}
