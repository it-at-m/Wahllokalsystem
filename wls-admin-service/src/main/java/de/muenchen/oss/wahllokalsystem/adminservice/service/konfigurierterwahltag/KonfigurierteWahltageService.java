package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlenClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KonfigurierteWahltageService {

    private final KonfigurierterWahltagClient konfigurierterWahltagClient;

    private final WahlenClient wahlenClient;

    private final ExceptionFactory exceptionFactory;

    private final KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_GetKonfigurierteWahltage')")
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() {
        return konfigurierterWahltagClient.getKonfigurierteWahltage();
    }

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_PostKonfigurierterWahltag')")
    public void postKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltagModel) {
        konfigurierterWahltagValidator.validateModel(konfigurierterWahltagModel);
        if (konfigurierterWahltagModel.active()) {
            try {
                wahlenClient.resetWahlen();
            } catch (final Exception exception) {
                log.error("#postKonfigurierterWahltag failed to reset elections:", exception);
                throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
            }
        }
        konfigurierterWahltagClient.postKonfigurierterWahltag(konfigurierterWahltagModel);
    }
}
