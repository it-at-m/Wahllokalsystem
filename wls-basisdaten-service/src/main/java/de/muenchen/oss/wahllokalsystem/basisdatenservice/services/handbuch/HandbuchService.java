package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.handbuch;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Handbuch;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.HandbuchRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.WahltagIdUndWahlbezirksart;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandbuchService {

    private final HandbuchValidator handbuchValidator;

    private final HandbuchModelMapper handbuchModelMapper;

    private final HandbuchRepository handbuchRepository;

    private final ExceptionFactory exceptionFactory;

    public byte[] getHandbuch(final HandbuchReferenceModel handbuchReference) {
        log.info("#getHandbuch > reference > {}", handbuchReference);

        handbuchValidator.validHandbuchReferenceOrThrow(handbuchReference);
        val handbuchID = handbuchModelMapper.toEntityID(handbuchReference);

        return findByIDOrThrowNoData(handbuchID).getHandbuch();
    }

    public void setHandbuch(final HandbuchWriteModel handbuchWriteModel) {
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
