package de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlenClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
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

    private final KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_GetKonfigurierteWahltage')")
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() {
        return konfigurierterWahltagClient.getKonfigurierteWahltage();
    }

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_PostKonfigurierterWahltag')")
    public void postKonfigurierterWahltag(final KonfigurierterWahltagModel konfigurierterWahltagModel) {
        konfigurierterWahltagValidator.validateModel(konfigurierterWahltagModel);
        if (konfigurierterWahltagModel.active()) {
            wahlenClient.resetWahlen();
        }
        konfigurierterWahltagClient.postKonfigurierterWahltag(konfigurierterWahltagModel);
    }
}
