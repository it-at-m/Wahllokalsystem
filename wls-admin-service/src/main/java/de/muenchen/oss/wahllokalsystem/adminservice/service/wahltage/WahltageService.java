package de.muenchen.oss.wahllokalsystem.adminservice.service.wahltage;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltagModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahltageClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahltageService {

    private final WahltageClient wahltageClient;

    @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_GetWahltage')")
    public List<WahltagModel> getWahltage() {
        return wahltageClient.getWahltage();
    }
}
