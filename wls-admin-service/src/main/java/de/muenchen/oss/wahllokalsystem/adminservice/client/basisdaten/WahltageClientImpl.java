package de.muenchen.oss.wahllokalsystem.adminservice.client.basisdaten;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.basisdaten.client.WahltageControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.WahltageClient;
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
public class WahltageClientImpl implements WahltageClient {

    private final ExceptionFactory exceptionFactory;

    private final WahltageControllerApi wahltageControllerApi;

    private final WahltagClientMapper wahltagClientMapper;

    @Override
    public List<WahltagModel> getWahltage() {
        log.debug("#getWahltage1");

        List<WahltagModel> wahltage;
        try {
            val wahltageDTO = wahltageControllerApi.getWahltage();

            if(wahltageDTO == null){
                return null;
            }

            wahltage = wahltagClientMapper.fromRemoteClientSetOfWahltagDTOtoListOfWahltagModel(wahltageDTO);
            log.debug("#getWahltage response from basisdaten: {}", wahltage);
        } catch (WlsException wlsEx) {
            log.debug("found WlsException: {}");
            throw wlsEx;
        } catch (Exception exception) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_BASISDATEN);
        }
        return wahltage;
    }
}
