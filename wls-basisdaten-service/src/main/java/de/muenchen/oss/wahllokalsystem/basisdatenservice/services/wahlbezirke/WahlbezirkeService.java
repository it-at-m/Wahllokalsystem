package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahlen.WahlenService;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag.WahltageService;
import java.util.Collection;
import java.util.List;
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

    private final WahlenService wahlenService;

    private final WahltageService wahltageService;

    private final WahlbezirkeValidator wahlbezirkeValidator;

    private final WahlbezirkeClient wahlbezirkeClient;

    private final WahlbezirkModelMapper wahlbezirkModelMapper;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahlbezirke')"
    )
    public List<WahlbezirkModel> getWahlbezirke(final String wahltagID) {
        log.info("#getWahlbezirke");
        wahlbezirkeValidator.validWahltagIDParamOrThrow(wahltagID);

        val wahltag = wahltageService.getWahltagByID(wahltagID);
        if (!wahlbezirkRepository.existsByWahltag(wahltag.wahltag())) {
            log.error("#getWahlbezirke: Für wahltagID {} waren keine Wahlbezirke in der Datenbank", wahltagID);
            val wahlbezirkeOfWahltag = wahlbezirkeClient.loadWahlbezirke(wahltag.wahltag(), wahltag.nummer());
            val wahlbezirkeAsEntities = wahlbezirkModelMapper.fromListOfWahlbezirkModeltoListOfWahlbezirkEntities(wahlbezirkeOfWahltag);

            val wahlenOfWahltag = wahlenService.getExistingWahlenOrderedByReihenfolge(wahltagID);
            wahlbezirkeAsEntities.forEach(wahlbezirk -> linkFirstMatchingWahl(wahlbezirk, wahlenOfWahltag));

            wahlbezirkRepository.saveAll(wahlbezirkeAsEntities);
        }
        return wahlbezirkModelMapper.fromListOfWahlbezirkEntityToListOfWahlbezirkModel(wahlbezirkRepository.findByWahltag(wahltag.wahltag()));
    }

    private void linkFirstMatchingWahl(final Wahlbezirk wahlbezirk, final Collection<WahlModel> wahlen) {
        val searchedWahl = wahlen.stream().filter(wahl -> wahlbezirk.getWahlnummer().equals(wahl.nummer())).findFirst().orElse(null);
        if (null != searchedWahl) {
            wahlbezirk.setWahlID(searchedWahl.wahlID());
        }
    }
}
