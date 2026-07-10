package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.stimmzettel.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StimmzettelService {

  private final StimmzettelValidator stimmzettelValidator;
  private final StimmzettelModelMapper stimmzettelModelMapper;
  private final StimmzettelRepository stimmzettelRepository;

  public List<StimmzettelOfTeamModel> getStimmzettel(final StimmzettelOwnerModel stimmzettelOwner) {
    stimmzettelValidator.validOrThrow(stimmzettelOwner);

    val entitiesFound =
        stimmzettelRepository.findByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
            stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());
    return entitiesFound.stream().map(stimmzettelModelMapper::toModel).toList();
  }

  @Transactional
  public void saveStimmzettel(
      final StimmzettelOwnerModel stimmzettelOwner,
      final List<StimmzettelOfTeamModel> stimmzettelToSave) {
    stimmzettelValidator.validOrThrow(stimmzettelOwner);
    stimmzettelValidator.validOrThrow(stimmzettelToSave);

    stimmzettelRepository.deleteByIdWahlbezirkIDAndIdWahlIDAndIdTeamID(
        stimmzettelOwner.wahlbezirkID(), stimmzettelOwner.wahlID(), stimmzettelOwner.teamID());

    val entitiesToSave =
        stimmzettelToSave.stream()
            .map(stimmzettel -> stimmzettelModelMapper.toEntity(stimmzettelOwner, stimmzettel))
            .toList();
    stimmzettelRepository.saveAll(entitiesToSave);
  }

  public int getAnzahlStimmzettel(final BezirkUndWahlID bezirkUndWahlID) {
    return stimmzettelRepository.countByIdWahlbezirkIDAndIdWahlID(
        bezirkUndWahlID.getWahlbezirkID(), bezirkUndWahlID.getWahlID());
  }
}
