package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.StimmzettelRepository;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StimmzettelService {

  private final StimmzettelRepository stimmzettelRepository;

  public List<StimmzettelModel> getStimmzettel(final StimmzettelOwnerModel stimmzettelOwner) {
    return null;
  }

  public void saveStimmzettel(
      final StimmzettelOwnerModel stimmzettelOwner,
      final List<StimmzettelModel> stimmzettelToSave) {}

  public int getAnzahlStimmzettel(final BezirkUndWahlID bezirkUndWahlID) {
    return 0;
  }
}
