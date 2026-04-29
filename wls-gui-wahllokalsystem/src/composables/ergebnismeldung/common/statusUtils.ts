import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const { getStatus } = useStatusService();

export function useStatusUtils() {
  async function loadStatusByWahlIdAndWahlbezirkId(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = false
  ) {
    return (
      (await getStatus(wahlID, wahlbezirkID, sendNotification)) ||
      getInitialStatus(wahlID, wahlbezirkID)
    );
  }

  function getInitialStatus(wahlID: string, wahlbezirkID: string) {
    return {
      bezirkUndWahlID: { wahlID, wahlbezirkID },
      schnellmeldung: _getDefaultMeldung(),
      niederschrift: _getDefaultMeldung(),
    };
  }

  function _getDefaultMeldung() {
    return {
      validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
      gedruckt: false,
      uebermittelt: undefined,
      sendeuhrzeit: undefined,
    };
  }

  return {
    loadStatusByWahlIdAndWahlbezirkId,
    getInitialStatus,
  };
}
