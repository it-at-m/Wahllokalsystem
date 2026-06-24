export interface WahllokalBenutzerRow {
  username: string;
  wahlbezirk: string;
  kennung: string;
}

/**
 * Antwort des Backends, wenn zum Wahltag keine Benutzer existieren. Wird nicht
 * als Benutzername interpretiert.
 */
export const NO_USERS_MESSAGE =
  "Keine Nutzer zum angegebenen Wahltag gefunden.";

export function useWahllokalBenutzerFormatter() {
  /**
   * Zerlegt den vom Backend gelieferten CSV-String (ein Benutzername je Zeile)
   * in nach Wahlbezirksnummer sortierte Zeilen. Jeder Benutzername hat die Form
   * `<Kennung>-<Wahlbezirksnummer>`; ohne Trenner wird der ganze Name als
   * Wahlbezirk angezeigt.
   */
  function parseBenutzer(csv: string): WahllokalBenutzerRow[] {
    return csv
      .split(/\r?\n/)
      .map((line) => line.trim())
      .filter((line) => line.length > 0 && line !== NO_USERS_MESSAGE)
      .map((username) => {
        const separatorIndex = username.lastIndexOf("-");
        return {
          username,
          wahlbezirk:
            separatorIndex >= 0 ? username.slice(separatorIndex + 1) : username,
          kennung: separatorIndex >= 0 ? username.slice(0, separatorIndex) : "",
        };
      })
      .sort((rowA, rowB) =>
        rowA.wahlbezirk.localeCompare(rowB.wahlbezirk, "de", { numeric: true })
      );
  }

  return { parseBenutzer };
}
