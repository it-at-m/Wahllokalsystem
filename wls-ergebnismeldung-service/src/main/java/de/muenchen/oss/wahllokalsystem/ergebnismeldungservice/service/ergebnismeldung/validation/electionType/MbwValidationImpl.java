package de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.electionType;

import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.domain.common.Stapelart;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ausdruck.MeldungsartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.common.WahlbezirkArtModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.WahlartModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.DefaultElectionTypeValidator;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.ergebnismeldung.validation.ElectionTypeValidation;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.mbw.MBWBedenklicheStimmzettelService;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.ErfassungStatusModel;
import de.muenchen.oss.wahllokalsystem.ergebnismeldungservice.service.stimmzettelerfassung.status.StimmzettelerfassungService;
import de.muenchen.oss.wahllokalsystem.wls.common.exception.WlsException;
import de.muenchen.oss.wahllokalsystem.wls.common.security.domain.BezirkUndWahlID;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MbwValidationImpl implements ElectionTypeValidation {

  private final DefaultElectionTypeValidator validator;

  private final MBWBedenklicheStimmzettelService mbwBedenklicheStimmzettelService;
  private final StimmzettelerfassungService stimmzettelerfassungService;

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

    boolean stacksValid =
        validator.checkValidation(
            WahlbezirkArtModel.UWB,
            wahlbezirkID,
            wahlID,
            waehlerverzeichnisNummer,
            necessaryStacks);
    boolean hasBedenklich = hasBedenklicheStimmzettel(wahlbezirkID, wahlID);

    // Stapelerfassung valid
    if (stacksValid && hasBedenklich) {
      return true;
    }

    // invalid in general or valid for DSE
    return isValidDSE(meldungsart, new BezirkUndWahlID(wahlID, wahlbezirkID));
  }

  @Override
  public boolean isValidBwb(
      final String wahlbezirkID,
      final String wahlID,
      final Long waehlerverzeichnisNummer,
      final MeldungsartModel meldungsart)
      throws WlsException {
    val necessaryStacks = buildNecessaryStack();

    boolean stacksValid =
        validator.checkValidation(
            WahlbezirkArtModel.BWB,
            wahlbezirkID,
            wahlID,
            waehlerverzeichnisNummer,
            necessaryStacks);
    boolean hasBedenklich = hasBedenklicheStimmzettel(wahlbezirkID, wahlID);

    // Stapelerfassung valid
    if (stacksValid && hasBedenklich) {
      return true;
    }

    // invalid in general or valid for DSE
    return isValidDSE(meldungsart, new BezirkUndWahlID(wahlID, wahlbezirkID));
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

  private boolean isValidDSE(
      final MeldungsartModel meldungsart, final BezirkUndWahlID bezirkUndWahlID) {
    Optional<ErfassungStatusModel> status =
        stimmzettelerfassungService.getStimmzettelerfassungStatusForValidation(bezirkUndWahlID);
    // Schnellmeldung
    if (MeldungsartModel.V3.equals(meldungsart)) {
      return status.map(ErfassungStatusModel::isStimmzettelerfassungAbgeschlossen).orElse(false);
    } else if (MeldungsartModel.V1.equals(meldungsart)) {
      // Niederschrift
      return status.map(ErfassungStatusModel::isBeschlussfassungAbgeschlossen).orElse(false);
    }
    return false;
  }
}
