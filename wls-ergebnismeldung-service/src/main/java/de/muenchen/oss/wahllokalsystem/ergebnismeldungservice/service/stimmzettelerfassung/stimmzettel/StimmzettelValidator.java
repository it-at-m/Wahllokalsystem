package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.stimmzettel;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.DataConflictException;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.TeamBezirkUndWahlIDModelValidator;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StimmzettelValidator {

  private final ExceptionFactory exceptionFactory;
  private final TeamBezirkUndWahlIDModelValidator teamBezirkUndWahlIDModelValidator;

  public void validOrThrow(final List<StimmzettelOfTeamModel> listOfStimmzettel) {
    if (listOfStimmzettel == null) {
      throw exceptionFactory.createFachlicheWlsException(ExceptionConstants.STIMMZETTEL_FEHLEN);
    }

    verifyThatStimmzettelKennungIsUnique(listOfStimmzettel);
  }

  public void validOrThrow(final TeamBezirkUndWahlIDModel stimmzettelOwner) {
    teamBezirkUndWahlIDModelValidator.isValidOrThrow(
        stimmzettelOwner,
        () ->
            exceptionFactory.createFachlicheWlsException(
                ExceptionConstants.STIMMZETTEL_OWNER_IDS_ARE_MISSING));
  }

  public void validOrThrow(final BezirkUndWahlID bezirkUndWahlID) {
    if (bezirkUndWahlID == null
        || StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID())
        || StringUtils.isBlank(bezirkUndWahlID.getWahlID())) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.STIMMZETTEL_ANZAHL_IDS_ARE_MISSING);
    }
  }

  private void verifyThatStimmzettelKennungIsUnique(
      final List<StimmzettelOfTeamModel> listOfStimmzettel) {
    if (listOfStimmzettel.size()
        != listOfStimmzettel.stream()
            .map(StimmzettelOfTeamModel::stimmzettelkennung)
            .distinct()
            .count()) {
      throw new DataConflictException(ExceptionConstants.STIMMZETTELKENNUNG_NON_UNIQUE);
    }
  }
}
