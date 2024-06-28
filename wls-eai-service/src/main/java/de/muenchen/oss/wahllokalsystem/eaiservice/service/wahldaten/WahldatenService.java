package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahldaten;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.Wahltag;
import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahldaten.WahltageRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahldaten.dto.WahltagDTO;
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

    private final WahltageRepository wahltageRepository;

    @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_LoadWahltage')")
    public Set<WahltagDTO> getWahltage(LocalDate wahltageIncludingSince) {
        return getWahltageIncludingSince(wahltageIncludingSince).stream().map(wahldatenMapper::toDTO).collect(Collectors.toSet());
    }

    private List<Wahltag> getWahltageIncludingSince(LocalDate wahltageIncludingSince) {
        return wahltageRepository.findByTagAfterOrTagEquals(wahltageIncludingSince, wahltageIncludingSince);
    }
}
