package de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.service.ereignis;

import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.domain.ereignis.EreignisseRepository;
import de.muenchen.oss.wahllokalsystem.vorfaelleundvorkommnisseservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EreignisService {

  private final EreignisseRepository ereignisseRepository;
  private final ExceptionFactory exceptionFactory;
  private final EreignisseModelMapper ereignisseModelMapper;
  private final EreignisValidator ereignisValidator;

  @PreAuthorize(
      "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_GetEreignisse')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
  public Optional<EreignisseModel> getEreignisse(@P("wahlbezirkID") final String wahlbezirkID) {
    log.info("#getEreignis");
    ereignisValidator.validWahlbezirkIDOrThrow(wahlbezirkID);

    val ereignisse = ereignisseRepository.findByWahlbezirkID(wahlbezirkID);

    if (ereignisse.isEmpty()) {
      return Optional.empty();
    } else {
      val ereignisseModel = ereignisseModelMapper.toModel(ereignisse.get());
      return Optional.of(ereignisseModel);
    }
  }

  @Transactional
  @PreAuthorize(
      "hasAuthority('VorfaelleUndVorkommnisse_BUSINESSACTION_PostEreignisse')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID, authentication)")
  public void postEreignisse(@P("param") EreignisseModel ereignisse) {
    log.info("#postEreignis");
    ereignisValidator.validEreignisAndWahlbezirkIDOrThrow(ereignisse);
    try {
      ereignisseRepository.deleteByWahlbezirkID(ereignisse.wahlbezirkID());
      ereignisseRepository.save(ereignisseModelMapper.toEntity(ereignisse));
    } catch (Exception e) {
      log.error("postEreignis: Ereignis konnte nicht gespeichert werden. " + e);
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.SAVEEREIGNIS_UNSAVABLE);
    }
  }
}
