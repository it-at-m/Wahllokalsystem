package de.muenchen.oss.wahllokalsystem.basisdatenservice.service.nachlieferungsbezirke;

import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.common.WahltagIdUndWahlbezirkId;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.Nachlieferungsbezirk;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.domain.nachlieferungsbezirke.NachlieferungsbezirkeRepository;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.basisdatenservice.service.wahlbezirke.WahlbezirkeValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NachlieferungsbezirkeService {

  private final NachlieferungsbezirkeRepository nachlieferungsbezirkeRepository;

  private final ExceptionFactory exceptionFactory;

  private final WahlbezirkeValidator wahlbezirkeValidator;

  @PreAuthorize("hasAuthority('Basisdaten_BUSINESSACTION_PostNachlieferungsbezirke')")
  @Transactional
  public void setNachlieferungsbezirke(
      final String wahltagID, final List<String> nachlieferungsbezirke) {
    log.debug("#setNachlieferungsbezirke");
    wahlbezirkeValidator.validWahltagIDParamOrThrow(wahltagID);

    val existingNachlieferungsbezirke =
        nachlieferungsbezirkeRepository.findByWahltagIdUndWahlbezirkId_WahltagID(wahltagID);
    if (!existingNachlieferungsbezirke.isEmpty()) {
      nachlieferungsbezirkeRepository.deleteAll(existingNachlieferungsbezirke);
    }
    val nachlieferungsbezirkeToSave =
        nachlieferungsbezirke.stream()
            .map(
                bezirk -> new Nachlieferungsbezirk(new WahltagIdUndWahlbezirkId(wahltagID, bezirk)))
            .toList();

    try {
      nachlieferungsbezirkeRepository.saveAll(nachlieferungsbezirkeToSave);
    } catch (final Exception e) {
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.POSTNACHLIEFERUNGSBEZIRKE_SPEICHERN_NICHT_ERFOLGREICH);
    }
  }

  @PreAuthorize(
      "hasAuthority('Basisdaten_BUSINESSACTION_GetNachlieferungsbezirke')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
  @Transactional
  public boolean isNachlieferungsbezirk(String wahltagID, @P("wahlbezirkID") String wahlbezirkID) {
    log.debug("#isNachlieferungsbezirk");
    wahlbezirkeValidator.validWahltagIDParamOrThrow(wahltagID);

    val optionalNachlieferungsbezirk =
        nachlieferungsbezirkeRepository.findById(
            new WahltagIdUndWahlbezirkId(wahltagID, wahlbezirkID));
    return optionalNachlieferungsbezirk.isPresent();
  }
}
