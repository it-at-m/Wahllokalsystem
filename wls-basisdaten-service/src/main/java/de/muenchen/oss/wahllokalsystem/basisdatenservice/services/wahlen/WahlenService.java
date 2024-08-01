package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpMethod;
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
        wahlenValidator.validWahltagIDParamOrThrow(wahltagID, HttpMethod.GET);
        val wahltag = wahltagRepository.findById(wahltagID);
        wahlenValidator.validateWahltagForSearchingWahltagID(wahltag);

        if(wahlRepository.countByWahltag(wahltag.get().getWahltag()) == 0){
            log.error("#getWahlen: Für wahltagID {} waren keine Wahlen in der Datenbank", wahltagID);
            var thewahlen = wahlenClient.getWahlen(wahltag.get().getWahltag(), wahltag.get().getNummer());
            List<Wahl> wahlEntities =  wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(thewahlen);
            var a = true;
            wahlRepository.saveAll(wahlEntities);
        }
        var allInRepo = wahlRepository.findAll();
        var foundInRepo = wahlRepository.findByWahltagOrderByReihenfolge(wahltag.get().getWahltag());
        var res = wahlModelMapper.fromListOfWahlEntityToListOfWahlModel(foundInRepo);
        return res;
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostWahlen')")
    @Transactional
    public void postWahlen(String wahltagID, List<WahlModel> wahlen) {
        log.info("#postWahlen");
        wahlenValidator.validWahltagIDParamOrThrow(wahltagID, HttpMethod.POST);
        try {
            wahlRepository.saveAll(wahlModelMapper.fromListOfWahlModeltoListOfWahlEntities(wahlen));
        } catch (Exception e) {
            log.error("#postWahlen: Die Wahlen konnten aufgrund eines Fehlers nicht gespeichert werden {}:", e);
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.CODE_POSTWAHLEN_UNSAVEABLE);
        }
    }
}
