package de.muenchen.oss.wahllokalsystem.adminservice.service.wahllokalbenutzer;

import java.util.List;

public interface WahllokalBenutzerClient {

    /**
     * Generiert Wahllokalbenutzer zum angegebenen Wahltag. Für jeden Benutzer der angelegt werden soll,
     * muss eine WahllokalUserInfo im Body vorhanden sein. *
     *
     * @param wahltagID
     * @param wahllokalUserInfoModel
     * @return Als Antwort wird eine CSV-Liste in Form eines Strings mit den generierten Benutzernamen
     *         zurückgegeben.
     */
    String generateAndExportWahllokalBenutzer(String wahltagID, List<WahllokalBenutzerModel> wahllokalUserInfoModel);

    /**
     * Löscht alle Wahllokalbenutzer zum angegebenen Wahltag unwiederruflich.
     *
     * @param wahltagID
     */
    void deleteWahllokalBenutzer(String wahltagID);

    /**
     * @param wahltagID
     * @return Liefert einen CSV-String der alle Wahllokalbenutzernamen zum angegebenen Wahltag enthält
     */
    String exportWahllokalBenutzer(String wahltagID);
}
