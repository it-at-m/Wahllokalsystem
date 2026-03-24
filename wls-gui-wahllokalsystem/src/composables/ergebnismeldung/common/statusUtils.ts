import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

export function useStatusUtils() {
  function getInitialStatus(wahlID: string, wahlbezirkID: string) {
    return {
      bezirkUndWahlID: { wahlID, wahlbezirkID },
      schnellmeldung: getDefaultMeldung(),
      niederschrift: getDefaultMeldung(),
    };
  }

  function getDefaultMeldung() {
    return {
      validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
      gedruckt: false,
      uebermittelt: undefined,
      sendeuhrzeit: undefined,
    };
  }

  return {
    getInitialStatus,
    getDefaultMeldung,
  };
}
