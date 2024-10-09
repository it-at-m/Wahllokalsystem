package de.muenchen.oss.wahllokalsystem.basisdatenservice.utils;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlageRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.referendumvorlagen.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.KandidatRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlaegeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.wahlvorschlag.WahlvorschlagRepository;
import lombok.val;

public class PersistingUtils {

    public static Wahlvorschlaege persistWahlvorschlaege(final WahlvorschlaegeRepository wahlvorschlaegeRepository,
            final WahlvorschlagRepository wahlvorschlagRepository,
            final KandidatRepository kandidatRepository,
            final Wahlvorschlaege wahlvorschlaegeToPersist) {
        val createdEntity = wahlvorschlaegeRepository.save(wahlvorschlaegeToPersist);
        wahlvorschlaegeToPersist.getWahlvorschlaege().forEach(wahlvorschlag -> {
            wahlvorschlagRepository.save(wahlvorschlag);
            kandidatRepository.saveAll(wahlvorschlag.getKandidaten());
        });

        return createdEntity;
    }

    public static Referendumvorlagen persistReferendumvorlagen(final ReferendumvorlagenRepository referendumvorlagenRepository,
            final ReferendumvorlageRepository referendumvorlageRepository,
            final Referendumvorlagen referendumvorlagenToPersist) {
        val savedReferendumvorlagen = referendumvorlagenRepository.save(referendumvorlagenToPersist);
        referendumvorlageRepository.saveAll(referendumvorlagenToPersist.getReferendumvorlagen());
        return savedReferendumvorlagen;
    }
}
