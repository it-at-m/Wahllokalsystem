package de.muenchen.oss.wahllokalsystem.eaiservice.service.wahllokalZustand;

import de.muenchen.oss.wahllokalsystem.eaiservice.domain.wahllokalzustand.WahllokalZustandRepository;
import de.muenchen.oss.wahllokalsystem.eaiservice.rest.wahllokalzustand.dto.WahllokalZustandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahllokalZustandService {

  private final WahllokalZustandValidator wahllokalZustandValidator;
  private final WahllokalZustandMapper wahllokalZustandMapper;
  private final WahllokalZustandRepository wahllokalZustandRepository;

  @PreAuthorize("hasAuthority('aoueai_BUSINESSACTION_SaveWahllokalZustand')")
  public void setWahllokalZustand(final WahllokalZustandDTO wahllokalZustandToSet) {
    wahllokalZustandValidator.validWahllokalZustandOrThrow(wahllokalZustandToSet);
    wahllokalZustandRepository.save(wahllokalZustandMapper.toEntity(wahllokalZustandToSet));
  }
}
