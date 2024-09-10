package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.wahltag;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Wahltag;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahltageService {

    private final ExceptionFactory exceptionFactory;

    private final WahltagRepository wahltagRepository;

    private final WahltagModelMapper wahltagModelMapper;

    private final WahltageClient wahltageClient;

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahltage')"
    )
    public List<WahltagModel> getWahltage() {
        log.info("#getWahltage");
        val wahltage = wahltageClient.getWahltage(LocalDate.now().minusMonths(3));
        wahltagRepository.saveAll(wahltagModelMapper.fromWahltagModelToWahltagEntityList(wahltage));
        return wahltagModelMapper.fromWahltagEntityToWahltagModelList(wahltagRepository.findAllByOrderByWahltagAsc());
    }

    @PreAuthorize(
        "hasAuthority('Basisdaten_BUSINESSACTION_GetWahltag')"
    )
    public WahltagModel getWahltagByID(final String wahltagID) {
        return wahltagModelMapper.toModel(getWahltagByIDOrThrow(wahltagID));
    }

    private Wahltag getWahltagByIDOrThrow(final String wahltagID) {
        return wahltagRepository.findById(wahltagID)
                .orElseThrow(() -> exceptionFactory.createFachlicheWlsException(ExceptionConstants.GETWAHLBEZIRKE_NO_WAHLTAG));
    }
}
