package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.DataConflictException;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StimmzettelValidator {

  private final ExceptionFactory exceptionFactory;

  public void validOrThrow(final List<StimmzettelOfTeamModel> stimmzettels) {
    verifyThatStimmzettelKennungIsUnique(stimmzettels);
  }

  public void validOrThrow(final StimmzettelOwnerModel stimmzettelOwner) {
    if (StringUtils.isBlank(stimmzettelOwner.wahlbezirkID())
        || StringUtils.isBlank(stimmzettelOwner.wahlID())
        || StringUtils.isBlank(stimmzettelOwner.teamID())) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.STIMMZETTEL_OWNER_IDS_ARE_MISSING);
    }
  }

  private void verifyThatStimmzettelKennungIsUnique(
      final List<StimmzettelOfTeamModel> stimmzettels) {
    if (stimmzettels.size()
        != stimmzettels.stream()
            .map(StimmzettelOfTeamModel::stimmzettelkennung)
            .distinct()
            .count()) {
      throw new DataConflictException(ExceptionConstants.STIMMZETTELKENNUNG_NON_UNIQUE);
    }
  }
}
