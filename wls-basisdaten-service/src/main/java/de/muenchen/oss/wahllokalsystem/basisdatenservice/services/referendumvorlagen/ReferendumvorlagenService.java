package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferendumvorlagenService {

    private final ReferendumvorlagenValidator referendumvorlagenValidator;

    private final ReferendumvorlagenModelMapper referendumvorlagenModelMapper;

    private final ReferendumvorlagenClient referendumvorlagenClient;

    private final ReferendumvorlageRepository referendumvorlageRepository;
    private final ReferendumvorlagenRepository referendumvorlagenRepository;

    private final TransactionTemplate transactionTemplate;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetReferendumvorlagen')")
    public ReferendumvorlagenModel getReferendumvorlagen(final ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        log.info("#getReferendumvorlagen");

        referendumvorlagenValidator.validReferumvorlageReferenceModelOrThrow(referendumvorlagenReferenceModel);

        val referendumBezirkUndWahlID = referendumvorlagenModelMapper.toBezirkUndWahlID(referendumvorlagenReferenceModel);
        val existingReferendumvorlagen = referendumvorlagenRepository.findByBezirkUndWahlID(referendumBezirkUndWahlID);

        if (existingReferendumvorlagen.isEmpty()) {
            return cacheReferendumvorlagen(referendumvorlagenReferenceModel, referendumBezirkUndWahlID);
        } else {
            return referendumvorlagenModelMapper.toModel(existingReferendumvorlagen.get());
        }
    }

    private ReferendumvorlagenModel cacheReferendumvorlagen(ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel,
            BezirkUndWahlID referendumBezirkUndWahlID) {
        val importedReferendumvorlagen = referendumvorlagenClient.getReferendumvorlagen(referendumvorlagenReferenceModel);

        val entitiesToSave = referendumvorlagenModelMapper.toEntity(importedReferendumvorlagen, referendumBezirkUndWahlID);
        saveReferendumvorlagen(entitiesToSave);

        return importedReferendumvorlagen;
    }

    private void saveReferendumvorlagen(final Referendumvorlagen referendumvorlagenToSave) {
        try {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                referendumvorlagenRepository.save(referendumvorlagenToSave);
                referendumvorlageRepository.saveAll(referendumvorlagenToSave.getReferendumvorlagen());
            });
        } catch (final Exception e) {
            log.error("#getReferendumvorlagen: Fehler beim Cachen", e);
        }
    }
}
