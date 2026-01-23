import type { Status } from "@/types/ergebnismeldung/common/Status.ts";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

export const storeID = "status";

export const useStatusStore = defineStore(storeID, () => {
  const { getStatus: _getStatus, postStatus } = useStatusService();

  const status = ref<Status[]>([]);
  const isStatusSaving = ref(false);

  //TODO: non election specific status - to complete
  const isWahlvorstandErfasst = ref(false);
  const isWahlumgebungErfasst = ref(false);

  const DEFAULT_MELDUNG = {
    validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
    gedruckt: false,
    uebermittelt: undefined,
    sendeuhrzeit: undefined,
  };

  function getStatus(wahlID: string, wahlbezirkID: string): Status | undefined {
    return status.value.find(
      (statusEntry) =>
        statusEntry.bezirkUndWahlID.wahlID === wahlID &&
        statusEntry.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    );
  }

  function isElectionFinished(wahlID: string, wahlbezirkID: string): boolean {
    const electionStatus = getStatus(wahlID, wahlbezirkID);
    return electionStatus ? electionStatus.niederschrift.gedruckt : false;
  }

  function isStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string
  ): boolean {
    return getStatus(wahlID, wahlbezirkID)?.stepsDone[step] ?? false;
  }

  async function loadStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const statusForWahl = await _getStatus(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      if (statusForWahl) {
        status.value.push(statusForWahl);
      } else {
        const newStatus = {
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          schnellmeldung: DEFAULT_MELDUNG,
          niederschrift: DEFAULT_MELDUNG,
          stepsDone: {},
        };
        status.value.push(newStatus);
      }
    } catch {
      throw Error(`Fehler beim Laden des Status für WahlID: ${wahlID}`);
    }
  }

  async function saveStatus(wahlID: string, wahlbezirkID: string) {
    isStatusSaving.value = true;
    try {
      for (const statusEntry of status.value) {
        await postStatus(wahlID, wahlbezirkID, statusEntry);
      }
    } catch {
      throw Error(`Fehler beim Speichern des Status für WahlID: ${wahlID}`);
    } finally {
      isStatusSaving.value = false;
    }
  }

  function setStepDone(
    wahlID: string,
    wahlbezirkID: string,
    step: string,
    isDone = true
  ) {
    const status = getStatus(wahlID, wahlbezirkID);
    if (status) {
      status.stepsDone[step] = isDone;
    }
  }

  return {
    status,
    isStatusSaving,
    isWahlvorstandErfasst,
    isWahlumgebungErfasst,
    getStatus,
    isElectionFinished,
    isStepDone,
    loadStatus,
    saveStatus,
    setStepDone,
  };
});

const { registerStoreHMR } = useHmrUpdate();
registerStoreHMR(useStatusStore);
