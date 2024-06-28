package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahlbezirkRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahldatenService {

    private final WahldatenMapper wahldatenMapper;

    private final IDConverter idConverter;

    private final WahltageRepository wahltageRepository;

    private final WahlRepository wahlRepository;

    private final WahlbezirkRepository wahlbezirkRepository;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahltage')")
    public Set<WahltagDTO> getWahltage(LocalDate wahltageIncludingSince) {
        return getWahltageIncludingSince(wahltageIncludingSince).stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlen')")
    public Set<WahlDTO> getWahlen(final LocalDate wahltag, final String nummer) {
        return wahlRepository.findByWahltagTagAndNummer(wahltag, nummer).stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlbezirke')")
    public Set<WahlbezirkDTO> getWahlbezirke(final LocalDate wahltag, final String nummer) {
        return wahlbezirkRepository.findWahlbezirkeWithWahlAndWahltagById(wahltag, nummer).stream().map(wahldatenMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahlberechtigte')")
    public List<WahlberechtigteDTO> getWahlberechtigte(final String wahlbezirkID) {
        return wahlbezirkRepository.findWahlbezirkeWithWahlAndWahltagById(idConverter.convertIDToUUIDOrThrow(wahlbezirkID)).stream()
                .map(wahldatenMapper::toWahlberechtigteDTO)
                .toList();
    }

    private List<Wahltag> getWahltageIncludingSince(LocalDate wahltageIncludingSince) {
        return wahltageRepository.findByTagAfterOrTagEquals(wahltageIncludingSince, wahltageIncludingSince);
    }
}
