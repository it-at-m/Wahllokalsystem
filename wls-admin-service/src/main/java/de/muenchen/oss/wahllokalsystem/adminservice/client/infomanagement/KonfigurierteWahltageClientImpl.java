package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.konfigurierterwahltag.KonfigurierteWahltageClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class KonfigurierteWahltageClientImpl implements KonfigurierteWahltageClient {

    private final ExceptionFactory exceptionFactory;
    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;
    private final KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Override
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() throws WlsException {
        final List<KonfigurierterWahltagDTO> konfigurierterWahltagDTOList;
        try {
            konfigurierterWahltagDTOList = konfigurierterWahltagControllerApi.getKonfigurierteWahltage();
        } catch (final Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }
        if (konfigurierterWahltagDTOList == null) {
            return null;
        }
        return konfigurierterWahltagDTOList.stream().map(konfigurierterWahltagClientMapper::toModel).toList();
    }
}
