package de.muenchen.oss.wahllokalsystem.adminservice.client.auth;

import de.muenchen.oss.wahllokalsystem.adminservice.configuration.Profiles;
import de.muenchen.oss.wahllokalsystem.adminservice.eai.auth.client.WahllokalBenutzerControllerApi;
import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerClient;
import de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer.WahllokalBenutzerModel;
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
public class WahllokalBenutzerClientImpl implements WahllokalBenutzerClient {

    private final ExceptionFactory exceptionFactory;
    private final WahllokalBenutzerControllerApi wahllokalBenutzerControllerApi;
    private final WahllokalBenutzerClientMapper wahllokalBenutzerClientMapper;

    @Override
    public String generateAndExportWahllokalBenutzer(final String wahltagID, List<WahllokalBenutzerModel> wahllokalBenutzerModels) {
        String csvBenutzer;

        log.debug("#generateAndExportWahllokalBenutzer {}", wahltagID);

        try {
            val wahllokalUserInfoDTOs = wahllokalBenutzerClientMapper.toListOfWahllokalUserInfoDTO(wahllokalBenutzerModels);
            csvBenutzer = wahllokalBenutzerControllerApi.createAndExportWahllokalBenutzer(wahltagID, wahllokalUserInfoDTOs);
            log.info("#generateAndExportWahllokalBenutzer, response: {}", csvBenutzer);
        } catch (final WlsException wlsException) {
            log.error("#generateAndExportWahllokalBenutzer found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#generateAndExportWahllokalBenutzer exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH);
        }
        return csvBenutzer;
    }

    @Override
    public void deleteWahllokalBenutzer(String wahltagID) {
        log.debug("#begin deleteWahllokalBenutzer {}", wahltagID);
        try {
            wahllokalBenutzerControllerApi.deleteWahllokalBenutzer(wahltagID);
        } catch (final WlsException wlsException) {
            log.error("#deleteWahllokalBenutzer found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#WahllokalBenutzer nicht geloescht. Exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH);
        }
    }

    @Override
    public String exportWahllokalBenutzer(String wahltagID) {
        log.debug("#begin exportWahllokalBenutzer {}", wahltagID);
        String csvBenutzer;
        try {
            csvBenutzer = wahllokalBenutzerControllerApi.exportWahllokalBenutzer(wahltagID);
            log.info("#exportWahllokalBenutzer, response: {}", csvBenutzer);
        } catch (final WlsException wlsException) {
            log.error("#exportWahllokalBenutzer found WlsException:", wlsException);
            throw wlsException;
        } catch (final Exception exception) {
            log.error("#WahllokalBenutzer nicht exportiert. Exception:", exception);
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.KOMMUNIKATIONSFEHLER_MIT_AUTH);
        }
        return csvBenutzer;
    }
}
