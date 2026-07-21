package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.teamstatus;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamErfassungStatusValidator {

  private final ExceptionFactory exceptionFactory;

  public void isValidOrThrow(final TeamBezirkUndWahlIDModel id) {
    if (id == null
        || StringUtils.isBlank(id.teamID())
        || StringUtils.isBlank(id.wahlbezirkID())
        || StringUtils.isBlank(id.wahlID())) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_INVALID_IDs);
    }
  }

  public void isValidOrThrow(final TeamErfassungStatusModel model) {
    if (model == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.STIMMZETTELERFASSUNG_TEAM_STATUS_SAVE_MODEL_IS_MISSING);
    }
  }
}
