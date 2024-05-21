package de.muenchen.oss.wahllokalsystem.infomanagementservice.service.wahltag;

import de.muenchen.oss.wahllokalsystem.infomanagementservice.domain.wahltag.KonfigurierterWahltagRepository;
import de.muenchen.oss.wahllokalsystem.infomanagementservice.exception.ExceptionDataWrapper;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.FachlicheWlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ServiceIDFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KonfigurierterWahltagService {

    private static final ExceptionDataWrapper DELETE_KONFIGURIERTERWAHLTAG_NOT_DELETEABLE = new ExceptionDataWrapper("105",
            "deleteKonfigurierterWahltag: Der konfigurierte Wahltag konnte nicht geloescht werden.");

    private final ServiceIDFormatter serviceIDFormatter;

    private final KonfigurierterWahltagRepository konfigurierterWahltagRepository;

    private final KonfigurierterWahltagModelMapper konfigurierterWahltagMapper;

    private final KonfigurierterWahltagValidator konfigurierterWahltagValidator;

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_GetKonfigurierterWahltag')")
    public KonfigurierterWahltagModel getKonfigurierterWahltag() {
        log.info("#getKonfigurierterWahltag");
        val entity = konfigurierterWahltagRepository.findByActive(true);
        return entity == null ? null : konfigurierterWahltagMapper.toModel(entity);
    }

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_PostKonfigurierterWahltag')")
    @Transactional
    public void setKonfigurierterWahltag(KonfigurierterWahltagModel konfigurierterWahltagModel) {
        log.info("#postKonfigurierterWahltag");
        konfigurierterWahltagValidator.validPostModelOrThrow(konfigurierterWahltagModel);
        val konfigurierterWahltagEntity = konfigurierterWahltagMapper.toEntity(konfigurierterWahltagModel);

        if (konfigurierterWahltagModel.active()) {
            konfigurierterWahltagRepository.setExistingKonfigurierteWahltageInaktiv();
        }

        konfigurierterWahltagRepository.save(konfigurierterWahltagEntity);
    }

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_DeleteKonfigurierterWahltag')")
    public void deleteKonfigurierterWahltag(final String wahltagID) {
        log.info("#deleteKonfigurierterWahltag");
        konfigurierterWahltagValidator.validDeleteModelOrThrow(wahltagID);

        try {
            konfigurierterWahltagRepository.deleteById(wahltagID);
        } catch (Exception e) {
            log.error("#deleteKonfigurierterWahltag undeleteable: " + e);
            throw FachlicheWlsException.withCode(DELETE_KONFIGURIERTERWAHLTAG_NOT_DELETEABLE.code()).inService(serviceIDFormatter.getId())
                    .buildWithMessage(
                            DELETE_KONFIGURIERTERWAHLTAG_NOT_DELETEABLE.message());
        }
    }

    @PreAuthorize("hasAuthority('Infomanagement_BUSINESSACTION_GetKonfigurierteWahltage')")
    public List<KonfigurierterWahltagModel> getKonfigurierteWahltage() {
        log.info("#getKonfigurierteWahltage");
        val konfigurierteWahltage = konfigurierterWahltagRepository.findAll(Sort.by(Sort.Direction.ASC, "wahltag"));

        return konfigurierterWahltagMapper.toModelList(konfigurierteWahltage);
    }

    public boolean isWahltagActive(final String wahltagID) {
        log.debug("#getLoginCheck");
        val konfigurierterWahltag = konfigurierterWahltagRepository.findById(wahltagID);

        return konfigurierterWahltag.isPresent() && konfigurierterWahltag.get().isActive();
    }
}
