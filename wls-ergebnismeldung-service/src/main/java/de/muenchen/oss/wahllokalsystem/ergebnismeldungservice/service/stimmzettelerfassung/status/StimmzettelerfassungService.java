package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatus;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.stimmzettelerfassung.status.StimmzettelerfassungStatusRepository;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StimmzettelerfassungService {

  private final StimmzettelerfassungStatusRepository stimmzettelerfassungStatusRepository;
  private final StimmzettelerfassungValidator stimmzettelerfassungValidator;
  private final ExceptionFactory exceptionFactory;
  private final ErfassungStatusModelMapper erfassungStatusModelMapper;

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_SaveStimmzettelerfassungStatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public void saveStimmzettelerfassungStatus(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID,
      final ErfassungStatusModel erfassungStatusModel) {
    stimmzettelerfassungValidator.validBezirkUndWahlIdOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.POST_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG));

    stimmzettelerfassungStatusRepository.save(
        new StimmzettelerfassungStatus(
            bezirkUndWahlID, erfassungStatusModelMapper.toEntity(erfassungStatusModel)));
  }

  @PreAuthorize(
      "hasAuthority('Ergebnismeldung_BUSINESSACTION_GetStimmzettelerfassungStatus')"
          + " and @bezirkIdPermissionEvaluator.tokenUserBezirkIdMatches(#bezirkUndWahl.wahlbezirkID, authentication)")
  public Optional<ErfassungStatusModel> getStimmzettelerfassungStatus(
      @P("bezirkUndWahl") final BezirkUndWahlID bezirkUndWahlID) {
    stimmzettelerfassungValidator.validBezirkUndWahlIdOrThrow(
        bezirkUndWahlID,
        exceptionFactory.createFachlicheWlsException(
            ExceptionConstants.GET_STIMMZETTELERFASSUNG_STATUS_PARAMETER_UNVOLLSTAENDIG));

    val optionalStimmzettelerfassungStatus =
        stimmzettelerfassungStatusRepository.findById(bezirkUndWahlID);
    return optionalStimmzettelerfassungStatus
        .map(StimmzettelerfassungStatus::getStatus)
        .map(erfassungStatusModelMapper::toModel);
  }
}
