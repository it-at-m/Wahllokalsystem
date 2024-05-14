package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltag;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
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

    private final KonfigurierterWahltagModelMapper konfigurierterWahltagMapper;

    private final KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    public KonfigurierterWahltagModel getKonfigurierterWahltag() {

        log.info("#getKonfigurierterWahltag");
        val entity = konfigurierterWahltagRepository.findByWahltagStatus(WahltagStatus.AKTIV);
        return entity == null ? null : konfigurierterWahltagMapper.toModel(entity);

    }

    public void setKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltagModel) {

        log.info("#postKonfigurierterWahltag");
        konfigurierterWahltagValidator.validModelOrThrow(konfigurierterWahltagModel);
        val konfigurierterWahltagEntity = konfigurierterWahltagMapper.toEntity(konfigurierterWahltagModel);

        if (konfigurierterWahltagModel.wahltagStatus() == null)
            konfigurierterWahltagEntity.setWahltagStatus(WahltagStatus.INAKTIV);

        if (konfigurierterWahltagModel.wahltagStatus() == WahltagStatus.AKTIV) {
            Iterable<KonfigurierterWahltag> wahltage = konfigurierterWahltagRepository.findAll();
            wahltage.forEach(wt -> wt.setWahltagStatus(WahltagStatus.INAKTIV));
            konfigurierterWahltagRepository.saveAll(wahltage);
        }

        konfigurierterWahltagRepository.save(konfigurierterWahltagEntity);
    }

}
