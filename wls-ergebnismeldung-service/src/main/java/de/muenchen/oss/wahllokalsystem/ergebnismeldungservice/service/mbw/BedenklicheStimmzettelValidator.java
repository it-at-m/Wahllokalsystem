package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.exception.ExceptionConstants;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.util.ExceptionFactory;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BedenklicheStimmzettelValidator {

  private final ExceptionFactory exceptionFactory;

  public void validateGetBedenklicheStimmzettelParameterOrThrow(
      final BezirkUndWahlID bezirkUndWahlID) {
    if (!isBezirkAndWahlIDValid(bezirkUndWahlID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.GET_BEDENKLICHE_STIMMZETTEL_PARAMETER_UNVOLLSTAENDIG);
    }
  }

  public void validateSetBedenklicheStimmzettelParameterOrThrow(
      final BezirkUndWahlID bezirkUndWahlID,
      final Collection<BedenklicherStimmzettelModel> bedenklicheStimmzettelToSave) {
    if (!isBezirkAndWahlIDValid(bezirkUndWahlID)) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.POST_BEDENKLICHE_STIMMZETTEL_UNVOLLSTAENDIG);
    }

    if (bedenklicheStimmzettelToSave == null) {
      throw exceptionFactory.createFachlicheWlsException(
          ExceptionConstants.POST_BEDENKLICHE_STIMMZETTEL_UNVOLLSTAENDIG);
    }
  }

  private boolean isBezirkAndWahlIDValid(final BezirkUndWahlID bezirkUndWahlID) {
    return bezirkUndWahlID != null
        && !StringUtils.isBlank(bezirkUndWahlID.getWahlID())
        && !StringUtils.isBlank(bezirkUndWahlID.getWahlbezirkID());
  }
}
