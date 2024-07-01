package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Referendumvorlagen;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.ReferendumvorlagenRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NoSearchResultFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.ReferendumvorlagenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahlvorschlagService {

    private final WahlvorschlagRepository wahlvorschlagRepository;

    private final ReferendumvorlagenRepository referendumvorlagenRepository;

    private final ExceptionFactory exceptionFactory;

    private final WahlvorschlagMapper wahlvorschlagMapper;

    private final WahlvorschlagValidator wahlvorschlagValidator;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlvorschlaege')")
    public WahlvorschlaegeDTO getWahlvorschlaegeForWahlAndWahlbezirk(final String wahlID, final String wahlbezirkID) {
        wahlvorschlagValidator.validateWahlbezirkIDOrThrow(wahlbezirkID);
        wahlvorschlagValidator.validateWahlIDOrThrow(wahlID);
        val wahlvorschlaege = findWahlvorschlaegeForWahlAndWahlbezirkOrThrow(wahlbezirkID, wahlID);
        return wahlvorschlagMapper.toDTO(wahlvorschlaege);
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadReferendumvorlagen')")
    public ReferendumvorlagenDTO getReferendumvorlagenForWahlAndWahlbezirk(final String wahlID, final String wahlbezirkID) {
        wahlvorschlagValidator.validateWahlbezirkIDOrThrow(wahlbezirkID);
        wahlvorschlagValidator.validateWahlIDOrThrow(wahlID);
        val referendumvorlagen = findReferendumvorlagenForWahlAndWahlbezirkOrThrow(wahlbezirkID, wahlID);
        return wahlvorschlagMapper.toDTO(referendumvorlagen);
    }

    private Wahlvorschlaege findWahlvorschlaegeForWahlAndWahlbezirkOrThrow(final String wahlbezirkID, final String wahlID) {
        return wahlvorschlagRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)
            .orElseThrow(() -> new NoSearchResultFoundException(Wahlvorschlaege.class, wahlbezirkID, wahlID));
    }

    public Referendumvorlagen findReferendumvorlagenForWahlAndWahlbezirkOrThrow(final String wahlID, final String wahlbezirkID) {
        return referendumvorlagenRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)
            .orElseThrow(() -> new NoSearchResultFoundException(Referendumvorlagen.class, wahlbezirkID, wahlID));
    }
}
