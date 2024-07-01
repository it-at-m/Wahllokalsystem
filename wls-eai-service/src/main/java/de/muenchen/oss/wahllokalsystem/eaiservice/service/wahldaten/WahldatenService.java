package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.StimmzettelgebietRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahlbezirk;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.BasisdatenDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlberechtigteDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahlbezirkDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
import de.muenchen.oss.wahllokalsystem.eaiservice.service.IDConverter;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class WahldatenService {

    private final WahldatenMapper wahldatenMapper;

    private final WahldatenValidator wahldatenValidator;

    private final IDConverter idConverter;

    private final WahltageRepository wahltageRepository;

    private final WahlRepository wahlRepository;

    private final WahlbezirkRepository wahlbezirkRepository;

    private final StimmzettelgebietRepository stimmzettelgebietRepository;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahltage')")
    public Set<WahltagDTO> getWahltage(LocalDate wahltageIncludingSince) {
        wahldatenValidator.validGetWahltageParameterOrThrow(wahltageIncludingSince);

        return getWahltageIncludingSince(wahltageIncludingSince).stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlen')")
    public Set<WahlDTO> getWahlen(final LocalDate wahltag, final String nummer) {
        wahldatenValidator.validGetWahlenParameterOrThrow(wahltag, nummer);

        return wahlRepository.findByWahltagTagAndNummer(wahltag, nummer).stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlbezirke')")
    public Set<WahlbezirkDTO> getWahlbezirke(final LocalDate wahltag, final String nummer) {
        wahldatenValidator.validGetWahlbezirkeParameterOrThrow(wahltag, nummer);

        return findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(wahltag, nummer).stream().map(wahldatenMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlberechtigte')")
    public List<WahlberechtigteDTO> getWahlberechtigte(final String wahlbezirkID) {
        wahldatenValidator.validGetWahlberechtigteParameterOrThrow(wahlbezirkID);

        return wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(idConverter.convertIDToUUIDOrThrow(wahlbezirkID)).stream()
                .map(wahldatenMapper::toWahlberechtigteDTO)
                .toList();
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadBasisdaten')")
    public BasisdatenDTO getBasisdaten(final LocalDate wahltag, final String nummer) {
        wahldatenValidator.validGetBasisdatenParameterOrThrow(wahltag, nummer);

        val wahlbezirkeWithParentEntities = findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(wahltag, nummer);

        val basisstrukturdaten = wahlbezirkeWithParentEntities.stream().map(wahldatenMapper::toBasisstrukturdatenDTO).collect(Collectors.toSet());
        val wahlen = getWahlen(wahltag, nummer);
        val wahlbezirke = wahlbezirkeWithParentEntities.stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
        val stimmzettelgebiete = stimmzettelgebietRepository.findByWahlWahltagTagAndWahlNummer(wahltag, nummer).stream().map(wahldatenMapper::toDTO).collect(
                Collectors.toSet());

        return new BasisdatenDTO(basisstrukturdaten, wahlen, wahlbezirke, stimmzettelgebiete);
    }

    private List<Wahlbezirk> findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(final LocalDate wahltag, final String nummer) {
        return wahlbezirkRepository.findWahlbezirkeWithStimmzettelgebietAndWahlAndWahltagById(wahltag, nummer);
    }

    private List<Wahltag> getWahltageIncludingSince(LocalDate wahltageIncludingSince) {
        return wahltageRepository.findByTagAfterOrTagEquals(wahltageIncludingSince, wahltageIncludingSince);
    }
}
