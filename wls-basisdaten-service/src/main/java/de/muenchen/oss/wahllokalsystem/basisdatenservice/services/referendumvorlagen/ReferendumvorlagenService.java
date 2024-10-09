package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlagen;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferendumvorlagenService {

    private final ReferendumvorlagenValidator referendumvorlagenValidator;

    private final ReferendumvorlagenModelMapper referendumvorlagenModelMapper;

    private final ReferendumvorlagenClient referendumvorlagenClient;

    private final ReferendumvorlagenRepository referendumvorlagenRepository;

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
        try {
            referendumvorlagenRepository.save(entitiesToSave);
        } catch (final Exception e) {
            log.error("#getReferendumvorlagen: Fehler beim Cachen", e);
        }

        return importedReferendumvorlagen;
    }
}
