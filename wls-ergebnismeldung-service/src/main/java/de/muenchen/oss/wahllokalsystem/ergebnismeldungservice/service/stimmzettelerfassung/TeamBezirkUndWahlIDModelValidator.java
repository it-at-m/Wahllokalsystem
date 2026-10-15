package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung;

import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamBezirkUndWahlIDModelValidator {

  public void isValidOrThrow(
      final TeamBezirkUndWahlIDModel id, final Supplier<WlsException> exceptionSupplier) {
    if (id == null
        || StringUtils.isBlank(id.teamID())
        || StringUtils.isBlank(id.wahlbezirkID())
        || StringUtils.isBlank(id.wahlID())) {
      throw exceptionSupplier.get();
    }
  }
}
