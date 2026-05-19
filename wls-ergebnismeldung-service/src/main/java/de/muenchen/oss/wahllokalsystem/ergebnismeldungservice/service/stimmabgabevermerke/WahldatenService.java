package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.WahldatenRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WahldatenService {

  private final WahldatenRepository wahldatenRepository;
  private final StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;

  public Optional<WahldatenModel> getWahldaten(
      final String wahlbezirkID, final String wahlID, final long wvzNummer) {
    val wahldaten =
        wahldatenRepository.findByNaturalId(
            new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, wvzNummer));
    return wahldaten.map(stimmabgabevermerkeModelMapper::toModel);
  }

  public void setWahldaten(WahldatenModel wahldaten) {
    val existingEntity =
        wahldatenRepository.findByNaturalId(
            new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                wahldaten.wahlbezirkID(),
                wahldaten.wahlID(),
                wahldaten.waehlerverzeichnisNummer()));
    val entityToSave = stimmabgabevermerkeModelMapper.toEntity(wahldaten);
    if (existingEntity.isPresent()) {
      entityToSave.setId(existingEntity.get().getId());
      wahldatenRepository.save(entityToSave);
    } else {
      wahldatenRepository.save(entityToSave);
    }
  }
}
