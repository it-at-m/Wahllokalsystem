package de.muenchen.oss.wahllokalsystem.adminservice.client.infomanagement;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.client.KonfigurierterWahltagControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.infomanagement.model.KonfigurierterWahltagDTO;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.KonfigurierterWahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahltermindaten.KonfigurierterWahltagClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile(Profiles.NOT + Profiles.DUMMY_CLIENTS)
public class KonfigurierterWahltagClientImpl implements KonfigurierterWahltagClient {

    private final ExceptionFactory exceptionFactory;

    private final KonfigurierterWahltagControllerApi konfigurierterWahltagControllerApi;

    private final KonfigurierterWahltagClientMapper konfigurierterWahltagClientMapper;

    @Override
    public void postKonfigurierterWahltag(final KonfigurierterWahltagModel konfigurierterWahltag) {
        log.debug("#postKonfigurierterWahltag");

        try {
            val konfigurierterWahltagDTO = konfigurierterWahltagClientMapper.toDto(konfigurierterWahltag);
            konfigurierterWahltagControllerApi.setKonfigurierterWahltag(konfigurierterWahltagDTO);
        } catch (final WlsException wlsException) {
            log.debug("#postKonfigurierterWahltag found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }
    }

    @Override
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() throws WlsException {
        final List<KonfigurierterWahltagDTO> konfigurierterWahltagDTOList;
        try {
            konfigurierterWahltagDTOList = konfigurierterWahltagControllerApi.getKonfigurierteWahltage();

            if (konfigurierterWahltagDTOList == null) {
                return Collections.emptyList();
            }

        } catch (WlsException wlsException) {
            log.debug("#getKonfigurierteWahltage found WlsException:", wlsException);
            throw wlsException;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }
        return konfigurierterWahltagDTOList.stream().map(konfigurierterWahltagClientMapper::toModel).toList();
    }

    @Override
    public void deleteKonfigurierterWahltag(final String wahltagID) {
        log.debug("#deleteKonfigurierterWahltag");

        try {
            konfigurierterWahltagControllerApi.deleteKonfigurierterWahltag(wahltagID);
        } catch (final WlsException wlsException) {
            log.debug("#deleteKonfigurierterWahltag found WlsException:", wlsException);
            throw wlsException;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_INFOMANAGEMENT);
        }
    }
}
