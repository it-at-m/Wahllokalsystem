package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModelValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamErfassungStatusValidator {

  private final ExceptionFactory exceptionFactory;
  private final TeamBezirkUndWahlIDModelValidator teamBezirkUndWahlIDModelValidator;

  public void isValidOrThrow(final TeamBezirkUndWahlIDModel id) {
    teamBezirkUndWahlIDModelValidator.isValidOrThrow(
        id,
        () ->
            exceptionFactory.createFachlicheWlsException(
                ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_INVALID_IDs));
  }

  public void isValidOrThrow(final TeamErfassungStatusModel model) {
    if (model == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_SAVE_MODEL_IS_MISSING);
    }
  }
}
