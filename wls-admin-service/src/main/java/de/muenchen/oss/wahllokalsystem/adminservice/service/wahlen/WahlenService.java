package de.muenchen.oss.wahllokalsystem.adminservice.service.wahlen;

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

    public List<WahlModel> getWahlen(String wahltagID) {
        return null;
    }

    public void updateWahlen(List<WahlModel> wahlen, String wahltagID) {
    }
}
