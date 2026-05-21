package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmabgabevermerke;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.BezirkUndWahlIDUndWaehlerverzeichnisnummer;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmabgabevermerke.StimmabgabevermerkeRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StimmabgabevermerkeService {

  private final StimmabgabevermerkeRepository stimmabgabevermerkeRepository;
  private final StimmabgabevermerkeModelMapper stimmabgabevermerkeModelMapper;
  private final StimmabgabevermerkeValidator stimmabgabevermerkeValidator;
  private final ExceptionFactory exceptionFactory;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmabgabevermerke')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#wahlbezirkID, authentication)")
  public Optional<StimmabgabevermerkeModel> getStimmabgabevermerke(
      @P("wahlbezirkID") final String wahlbezirkID, final String wahlID, final long wvzNummer) {
    stimmabgabevermerkeValidator.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(
        new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, wvzNummer),
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.GET_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG));
    val wahldaten =
        stimmabgabevermerkeRepository.findByNaturalId(
            new BezirkUndWahlIDUndWaehlerverzeichnisnummer(wahlbezirkID, wahlID, wvzNummer));
    return wahldaten.map(stimmabgabevermerkeModelMapper::toModel);
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_PostStimmabgabevermerke')"
          + "and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#param?.wahlbezirkID(), authentication)")
  public void postStimmabgabevermerke(@P("param") StimmabgabevermerkeModel wahldaten) {
    stimmabgabevermerkeValidator.validBezirkIDUndWaehlerverzeichnisnummerOrThrow(
        new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
            wahldaten.wahlbezirkID(), wahldaten.wahlID(), wahldaten.waehlerverzeichnisNummer()),
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_STIMMABGABEVERMERKE_PARAMETER_UNVOLLSTAENDIG));
    stimmabgabevermerkeValidator.validStimmabgabevermerkeOrThrow(wahldaten);

    try {
      val existingEntity =
          stimmabgabevermerkeRepository.findByNaturalId(
              new BezirkUndWahlIDUndWaehlerverzeichnisnummer(
                  wahldaten.wahlbezirkID(),
                  wahldaten.wahlID(),
                  wahldaten.waehlerverzeichnisNummer()));
      val entityToSave = stimmabgabevermerkeModelMapper.toEntity(wahldaten);
      if (existingEntity.isPresent()) {
        entityToSave.setId(existingEntity.get().getId());
        stimmabgabevermerkeRepository.save(entityToSave);
      } else {
        stimmabgabevermerkeRepository.save(entityToSave);
      }
    } catch (final Exception e) {
      log.error("#postStimmabgabevermerke unsaveable:", e);
      throw exceptionFactory.createTechnischeWlsException(
          ExceptionConstants.STIMMABGABEVERMERKE_UNSAVEABLE);
    }
  }
}
