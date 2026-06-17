package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ElectionTypeValidation;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MbwValidationImpl implements ElectionTypeValidation {

  private final DefaultElectionTypeValidator validator;

  private final MBWBedenklicheStimmzettelService mbwBedenklicheStimmzettelService;

  @Override
  public boolean supportsWahlart(final WahlartModel wahlart) {
    return WahlartModel.MBW == wahlart;
  }

  @Override
  public boolean isValidUwb(
      final String wahlbezirkID,
      final String wahlID,
      final Long waehlerverzeichnisNummer,
      final MeldungsartModel meldungsart)
      throws WlsException {
    val necessaryStacks = buildNecessaryStack();
    return validator.checkValidation(
            WahlbezirkArtModel.UWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks)
        && hasBedenklicheStimmzettel(wahlbezirkID, wahlID);
  }

  @Override
  public boolean isValidBwb(
      final String wahlbezirkID,
      final String wahlID,
      final Long waehlerverzeichnisNummer,
      final MeldungsartModel meldungsart)
      throws WlsException {
    val necessaryStacks = buildNecessaryStack();
    return validator.checkValidation(
            WahlbezirkArtModel.BWB, wahlbezirkID, wahlID, waehlerverzeichnisNummer, necessaryStacks)
        && hasBedenklicheStimmzettel(wahlbezirkID, wahlID);
  }

  private List<Stapelart> buildNecessaryStack() {
    List<Stapelart> necessaryStacks = new ArrayList<>();
    necessaryStacks.add(Stapelart.MBW_A);
    necessaryStacks.add(Stapelart.MBW_B);
    necessaryStacks.add(Stapelart.MBW_D_UNGUELTIG);
    return necessaryStacks;
  }

  private boolean hasBedenklicheStimmzettel(final String wahlbezirkID, final String wahlID) {
    return mbwBedenklicheStimmzettelService.hasBedenklicheStimmzettel(
        new BezirkUndWahlID(wahlID, wahlbezirkID));
  }
}
