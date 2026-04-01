import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import { ref } from "vue";

import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

const { getStatus } = useStatusService();

export function useStatusUtils() {
  const status = ref<Status>();

  async function loadStatusToUpdate(wahlID: string, wahlbezirkID: string) {
    status.value =
      (await getStatus(wahlID, wahlbezirkID, false)) ||
      _getInitialStatus(wahlID, wahlbezirkID);
  }

  function _getInitialStatus(wahlID: string, wahlbezirkID: string) {
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
    status,
    loadStatusToUpdate,
    getDefaultMeldung,
  };
}
