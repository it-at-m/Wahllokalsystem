package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlenControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen.WahlenClient;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
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
public class WahlenClientImpl implements WahlenClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlenControllerApi wahlenControllerApi;

    private final WahlenClientMapper wahlenClientMapper;

    @Override
    public List<WahlModel> getWahlen(String wahltagID) throws WlsException {

        log.debug("#getWahlen");

        List<WahlModel> wahlen;
        try {
            val wahlenDTO = wahlenControllerApi.getWahlen(wahltagID);

            if (wahlenDTO == null) {
                return null;
            }
            wahlen = wahlenClientMapper.toModelList(wahlenDTO);
        } catch (final WlsException wlsException) {
            log.error("#getWahlen found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#getWahlen exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
        return wahlen;
    }

    @Override
    public void postWahlen(String wahltagID, List<WahlModel> wahlen) {
        log.debug("#postWahlen");

        try {
            wahlenControllerApi.postWahlen(wahltagID, wahlenClientMapper.toDtoList(wahlen));
        } catch (final WlsException wlsException) {
            log.error("#postWahlen found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#postWahlen exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
    }
}
