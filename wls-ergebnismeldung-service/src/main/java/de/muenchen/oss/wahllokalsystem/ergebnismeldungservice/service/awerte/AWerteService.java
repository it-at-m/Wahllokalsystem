package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.awerte;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.awerte.AWerteRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AWerteService extends AbstractAWerteService {

    private final AsyncAWerteService asyncAWerteService;

    public AWerteService(AWerteRepository aWerteRepository, AWerteValidator aWerteValidator, AWerteModelMapper aWerteModelMapper, AWerteClient aWerteClient,
            ExceptionFactory exceptionFactory, AsyncAWerteService asyncAWerteService) {
        super(aWerteRepository, aWerteValidator, aWerteModelMapper, aWerteClient, exceptionFactory);
        this.asyncAWerteService = asyncAWerteService;
    }

    @PreAuthorize("hasAuthority('Ergebnismeldung_BUSINESSACTION_GetAWerte') OR hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public List<AWerteModel> getAWerte(String wahlbezirkID) {
        return super.getAWerte(wahlbezirkID);
    }

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_LoadWahltermindaten')")
    public void initialiseAWerte(List<String> wahlbezirkIDs) {
        asyncAWerteService.initialiseAWerte(wahlbezirkIDs);
    }

}
