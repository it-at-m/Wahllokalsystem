package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahlbezirkeControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkeClient;
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
public class WahlbezirkeClientImpl implements WahlbezirkeClient {

    private final ExceptionFactory exceptionFactory;

    private final WahlbezirkeControllerApi wahlbezirkeControllerApi;

    private final WahlbezirkeClientMapper wahlbezirkeClientMapper;

    @Override
    public List<WahlbezirkModel> getWahlbezirke(String wahltagID) {
        log.debug("#getWahlbezirke");

        final List<WahlbezirkModel> wahlbezirke;
        try {
            val wahlbezirkeDTO = wahlbezirkeControllerApi.getWahlbezirke(wahltagID);

            if (wahlbezirkeDTO == null) {
                return null;
            }

            wahlbezirke = wahlbezirkeClientMapper.toModelList(wahlbezirkeDTO);
        } catch (final WlsException wlsException) {
            log.error("#getWahlbezirke found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#getWahlbezirke exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
        return wahlbezirke;
    }
}
