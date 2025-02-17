package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

import de.muenchen.oss.wahllokalsystem.adminservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahlenService {

    private final ExceptionFactory exceptionFactory;

    private final WahlenValidator wahlenValidator;

    private final WahlenClient wahlenClient;

    public List<WahlModel> getWahlen(String wahltagID) {
        wahlenValidator.validWahlIDParamOrThrow(wahltagID);

        return wahlenClient.getWahlen(wahltagID);
    }

    public void updateWahlen(List<WahlModel> wahlen, String wahltagID) {
        if (wahlen == null) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.MISSING_ARGUMENT);
        }
        wahlenClient.postWahlen(wahltagID, wahlen);
    }
}
