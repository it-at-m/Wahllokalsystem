package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkModel;
import de.muenchen.oss.wahllokalsystem.adminservice.service.common.WahlbezirkeClient;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WahllokalBenutzerService {

  private final WahllokalBenutzerValidator wahllokalbenutzerValidator;

  private final WahlbezirkeClient wahlbezirkeClient;
  private final WahllokalBenutzerClient wahllokalBenutzerClient;

  @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_GenerateWahllokalbenutzer')")
  public CsvFileModel generateWahllokalbenutzer(final String wahltagID) {
    wahllokalbenutzerValidator.validWahltagIDParamOrThrow(wahltagID);

    val wahlbezirke = wahlbezirkeClient.getWahlbezirke(wahltagID);

    wahllokalbenutzerValidator.wahlbezirkeExistOrThrow(wahlbezirke);

    val sortedWahlbezirke =
        wahlbezirke.stream()
            .sorted(Comparator.comparing(WahlbezirkModel::wahlnummer))
            .collect(Collectors.groupingBy(WahlbezirkModel::nummer))
            .values()
            .stream()
            .flatMap(List::stream)
            .toList();

    log.debug("generateWahllokalbenutzer, Anzahl Wahlbezirke: {}", sortedWahlbezirke.size());
    List<WahllokalBenutzerModel> userModels = generateBenutzerModels(sortedWahlbezirke);
    log.debug("generateWahllokalbenutzer, Anzahl generierter Benutzer: {}", userModels.size());

    return new CsvFileModel(
        wahllokalBenutzerClient.generateAndExportWahllokalBenutzer(wahltagID, userModels));
  }

  @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_ExportWahllokalBenutzer')")
  public CsvFileModel exportWahllokalBenutzer(final String wahltagID) {
    wahllokalbenutzerValidator.validWahltagIDParamOrThrow(wahltagID);

    return new CsvFileModel(wahllokalBenutzerClient.exportWahllokalBenutzer(wahltagID));
  }

  @PreAuthorize("hasAuthority('Admin_BUSINESSACTION_DeleteWahllokalBenutzer')")
  public void deleteWahllokalBenutzer(String wahltagID) {
    wahllokalbenutzerValidator.validWahltagIDParamOrThrow(wahltagID);

    wahllokalBenutzerClient.deleteWahllokalBenutzer(wahltagID);
  }

  private static List<WahllokalBenutzerModel> generateBenutzerModels(
      List<WahlbezirkModel> wahlbezirke) {
    List<WahllokalBenutzerModel> userModels = new ArrayList<>();

    for (WahlbezirkModel wb : wahlbezirke) {
      Optional<WahllokalBenutzerModel> foundUserOfWahllokalNummer =
          userModels.stream().filter(u -> u.wahlbezirknummer().equals(wb.nummer())).findAny();

      if (foundUserOfWahllokalNummer.isPresent()) {
        foundUserOfWahllokalNummer
            .get()
            .wbid_wahlnummer()
            .add(
                new TripleOfWahlbezirkIDWahlnummerWahlIDModel(
                    wb.wahlbezirkID(), wb.wahlnummer(), wb.wahlID()));

      } else {
        ArrayList<TripleOfWahlbezirkIDWahlnummerWahlIDModel> initWbIdWahlnummerWahlIDModel =
            new ArrayList<>();
        initWbIdWahlnummerWahlIDModel.add(
            new TripleOfWahlbezirkIDWahlnummerWahlIDModel(
                wb.wahlbezirkID(), wb.wahlnummer(), wb.wahlID()));
        userModels.add(
            new WahllokalBenutzerModel(
                wb.wahlbezirkID(),
                wb.nummer(),
                wb.wahltag(),
                wb.wahlbezirkart(),
                initWbIdWahlnummerWahlIDModel));
      }
    }
    return userModels;
  }
}
