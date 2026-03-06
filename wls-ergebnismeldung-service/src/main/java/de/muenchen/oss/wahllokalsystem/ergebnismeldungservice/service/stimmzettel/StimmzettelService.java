package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettel.StimmzettelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StimmzettelService {

  private final StimmzettelRepository stimmzettelRepository;
  private final StimmzettelModelMapper stimmzettelMapper;

  public List<StimmzettelModel> getStimmzettel(final String wahlID, final String wahlbezirkID) {
    return stimmzettelRepository
        .findByCombinedId_WahlIDAndCombinedId_WahlbezirkID(wahlID, wahlbezirkID)
        .stream()
        .map(stimmzettelMapper::toModel)
        .toList();
  }

  public void saveStimmzettel(final List<StimmzettelModel> stimmzettel) {
    val entitiesToSave = stimmzettel.stream().map(stimmzettelMapper::toEntity).toList();
    stimmzettelRepository.saveAll(entitiesToSave);
  }
}
