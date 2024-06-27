package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahlvorschlag;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.Wahlvorschlaege;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorschlag.WahlvorschlagRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahlvorstand.Wahlvorstand;
import de.muenchen.oss.wahllokalsystem.eaiservice.exception.NotFoundException;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.common.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahlvorschlag.dto.WahlvorschlaegeDTO;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahlvorschlagService {

    private final WahlvorschlagRepository wahlvorschlagRepository;

    private final ExceptionFactory exceptionFactory;

    private final WahlvorschlagMapper wahlvorschlagMapper;

    private final WahlvorschlagValidator wahlvorschlagValidator;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlvorschlag')")
    public WahlvorschlaegeDTO getWahlvorschlaegeForWahlAndWahlbezirk(final String wahlID, final String wahlbezirkID) {
        wahlvorschlagValidator.validateWahlbezirkIDOrThrow(wahlbezirkID);
        wahlvorschlagValidator.validateWahlIDOrThrow(wahlID);
        //val wahlUUID = convertIDToUUIDOrThrow(wahlID);
        //val wahlbezirkUUID = convertIDToUUIDOrThrow(wahlbezirkID);
        val wahlvorschlaege = findByWahlbezirkIDAndWahlIDOrThrow(wahlbezirkID, wahlID);
        return wahlvorschlagMapper.toDTO(wahlvorschlaege);
    }

    //CHECKSTYLE.OFF: AbbreviationAsWordInName - illegal match cause UUID should not be shortened
    private UUID convertIDToUUIDOrThrow(final String id) {
        //CHECKSTYLE.ON: AbbreviationAsWordInName
        try {
            return UUID.fromString(id);
        } catch (final IllegalArgumentException illegalArgumentException) {
            throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.ID_NICHT_KONVERTIERBAR);
        }
    }

    private Wahlvorschlaege findByWahlbezirkIDAndWahlIDOrThrow(final String wahlbezirkID, final String wahlID) {
        return wahlvorschlagRepository.findFirstByWahlbezirkIDAndWahlID(wahlbezirkID, wahlID)
            .orElseThrow(() -> new NotFoundException(convertIDToUUIDOrThrow(wahlbezirkID), Wahlvorstand.class));
    }

}
