package de.muenchen.oss.wahllokalsystem.basisdatenservice.services.referendumvorlage;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.ReferendumvorlagenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferendumvorlageService {

    private final ReferendumvorlageValidator referendumvorlageValidator;

    private final ReferendumvorlageModelMapper referendumvorlageModelMapper;

    private final ReferendumvorlagenClient referendumvorlagenClient;

    private final ReferendumvorlageRepository referendumvorlageRepository;
    private final ReferendumvorlagenRepository referendumvorlagenRepository;

    @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_GetReferendumvorlagen')")
    public ReferendumvorlagenModel loadReferendumvorlagen(final ReferendumvorlagenReferenceModel referendumvorlagenReferenceModel) {
        log.info("#getReferendumvorlagen");

        referendumvorlageValidator.validReferumvorlageReferenceModelOrThrow(referendumvorlagenReferenceModel);

        val referendumBezirkUndWahlID = referendumvorlageModelMapper.toBezirkUndWahlID(referendumvorlagenReferenceModel);
        val existingReferendumvorlagen = referendumvorlagenRepository.findById(referendumBezirkUndWahlID);

        if (existingReferendumvorlagen.isEmpty()) {
            val importedReferendumvorlagen = referendumvorlagenClient.getReferendumvorlagen(referendumvorlagenReferenceModel);

            val entitiesToSave = referendumvorlageModelMapper.toEntity(importedReferendumvorlagen, referendumBezirkUndWahlID);
            saveReferendumvorlagen(entitiesToSave);

            return importedReferendumvorlagen;
        } else {
            return referendumvorlageModelMapper.toModel(existingReferendumvorlagen.get());
        }
    }

    private void saveReferendumvorlagen(final Referendumvorlagen referendumvorlagenToSave) {
        try {
            referendumvorlagenRepository.save(referendumvorlagenToSave);
            referendumvorlageRepository.saveAll(referendumvorlagenToSave.getReferendumvorlagen());
        } catch (final Exception e) {
            log.error("#getReferendumvorlagen: Fehler beim Cachen", e);
        }
    }
}
