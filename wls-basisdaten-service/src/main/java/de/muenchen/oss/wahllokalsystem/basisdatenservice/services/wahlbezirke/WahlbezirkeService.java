package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.Wahl;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahl.WahlRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collection;
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
public class WahlbezirkeService {

    private final WahlbezirkRepository wahlbezirkRepository;

    private final WahlRepository wahlRepository;

    private final WahltagRepository wahltagRepository;

    private final WahlbezirkeValidator wahlbezirkeValidator;

    private final WahlbezirkeClient wahlbezirkeClient;

    private final ExceptionFactory exceptionFactory;

    private final WahlbezirkModelMapper wahlbezirkModelMapper;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlbezirke')"
    )
    public List<WahlbezirkModel> getWahlbezirke(final String wahltagID) {
        log.info("#getWahlbezirke");
        wahlbezirkeValidator.validWahltagIDParamOrThrow(wahltagID);

        Optional<Wahltag> wahltag = wahltagRepository.findById(wahltagID);
        wahlbezirkeValidator.validateWahltagForSearchingWahltagID(wahltag);
        if (wahltag.isPresent()) {
            val wahltagObjekt = wahltag.get();
            if (wahlbezirkRepository.countByWahltag(wahltagObjekt.getWahltag()) == 0) {
                log.error("#getWahlbezirke: Für wahltagID {} waren keine Wahlbezirke in der Datenbank", wahltagID);
                val wahlbezirkeOfWahltag = wahlbezirkeClient.loadWahlbezirke(wahltagObjekt.getWahltag(), wahltagObjekt.getNummer());
                val wahlbezirkeAsEntities = wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(wahlbezirkeOfWahltag);

                val wahlenOfWahltag = wahlRepository.findByWahltagOrderByReihenfolge(wahltagObjekt.getWahltag());
                wahlbezirkeAsEntities.forEach(wahlbezirk -> linkFirstMatchingWahl(wahlbezirk, wahlenOfWahltag));

                wahlbezirkRepository.saveAll(wahlbezirkeAsEntities);
            }
            return wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(wahlbezirkRepository.findByWahltag(wahltagObjekt.getWahltag()));
        } else {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBEZIRKE_NO_WAHLTAG);
        }
    }

    private void linkFirstMatchingWahl(final Wahlbezirk wahlbezirk, final Collection<Wahl> wahlen) {
        val searchedWahl = wahlen.stream().filter(wahl -> wahlbezirk.getWahlnummer().equals(wahl.getNummer())).findFirst().orElse(null);
        if (null != searchedWahl) {
            wahlbezirk.setWahlID(searchedWahl.getWahlID());
        }
    }
}
