package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.KonfigurierterWahltagMapper;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.rest.wahltag.WahltagStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KonfigurierterWahltagService {

    private final KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    private final KonfigurierterWahltagMapper mapper;

    public KonfigurierterWahltagModel getKonfigurierterWahltag(Object headers) {
        
        log.info("#getKonfigurierterWahltag");

        val konfigurierterWahltagFromRepo = konfigurierterWahltagRepository.findByWahltagStatus(WahltagStatus.AKTIV);

        return konfigurierterWahltagFromRepo == null ? null : mapper.toModel(konfigurierterWahltagFromRepo);

    }

}
