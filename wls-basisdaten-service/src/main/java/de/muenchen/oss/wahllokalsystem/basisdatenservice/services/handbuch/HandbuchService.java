package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.handbuch.HandbuchRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandbuchService {

    private final HandbuchValidator handbuchValidator;

    private final HandbuchModelMapper handbuchModelMapper;

    private final HandbuchRepository handbuchRepository;

    private final ExceptionFactory exceptionFactory;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetHandbuch')")
    public byte[] getHandbuch(final HandbuchReferenceModel handbuchReference) {
        log.info("#getHandbuch - handbuchReference > {}", handbuchReference);

        handbuchValidator.validHandbuchReferenceOrThrow(handbuchReference);
        val handbuchID = handbuchModelMapper.toEntityID(handbuchReference);

        val handbuchData = findByIDOrThrowNoData(handbuchID).getHandbuch();
        return Arrays.copyOf(handbuchData, handbuchData.length);
    }

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostHandbuch')")
    public void setHandbuch(final HandbuchWriteModel handbuchWriteModel) {
        log.info("postHandbuch - handbuchWriteModel> {}", handbuchWriteModel);
        handbuchValidator.validHandbuchWriteModelOrThrow(handbuchWriteModel);
        val entityToSave = handbuchModelMapper.toEntity(handbuchWriteModel);
        try {
            handbuchRepository.save(entityToSave);
        } catch (final Exception e) {
            throw exceptionFactory.createTechnischeWlsException(ExceptionConstants.POSTHANDBUCH_SPEICHERN_NICHT_ERFOLGREICH);
        }
    }

    private Handbuch findByIDOrThrowNoData(final WahltagIdUndWahlbezirksart handbuchReference) {
        return handbuchRepository.findById(handbuchReference)
                .orElseThrow(() -> exceptionFactory.createTechnischeWlsException(ExceptionConstants.GETHANDBUCH_KEINE_DATEN));
    }
}
