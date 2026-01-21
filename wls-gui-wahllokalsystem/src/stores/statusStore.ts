import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { Ref } from "vue";

import { defineStore } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useStatusService } from "@/composables/ergebnismeldung/common/statusService.ts";
import { MeldungValidierungsstatusEnum } from "@/types/ergebnismeldung/common/MeldungValidierungsstatusEnum.ts";

export const storeID = "status";

export interface WahlState extends Record<string, boolean> {
  schnellmeldungGesendet: boolean;
  schnellmeldungGedruckt: boolean;
  niederschriftGesendet: boolean;
  niederschriftGedruckt: boolean;
}

export interface MBWState extends WahlState {
  stimmzettelgezaehlt: boolean;
  gueltigeErfasst: boolean;
  ungueltigeErfasst: boolean;
  kandidatenErfasst: boolean;
}

export const useStatusStore = defineStore(storeID, () => {
  const { getStatus, postStatus } = useStatusService();

  const status = ref<Status[]>([]);
  const isStatusSaving = ref(false);

  //TODO: non election specific status - to complete
  const isWahlvorstandErfasst = ref(false);
  const isWahlumgebungErfasst = ref(false);

  const wahlStatus: Ref<Map<string, Ref<WahlState>>> = ref(new Map());

  const DEFAULT_MELDUNG = {
    validierungsstatus: MeldungValidierungsstatusEnum.NichtValidiert,
    gedruckt: false,
    uebermittelt: undefined,
    sendeuhrzeit: undefined,
  };

  async function loadStatus(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const statusForWahl = await getStatus(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      if (statusForWahl) {
        status.value.push(statusForWahl);
      } else {
        status.value.push({
          bezirkUndWahlID: { wahlID, wahlbezirkID },
          schnellmeldung: DEFAULT_MELDUNG,
          niederschrift: DEFAULT_MELDUNG,
        });
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

  function addWahl(wahlID: string) {
    if (!wahlStatus.value.get(wahlID)) {
      wahlStatus.value.set(
        wahlID,
        ref({
          niederschriftGedruckt: false,
          niederschriftGesendet: false,
          schnellmeldungGedruckt: false,
          schnellmeldungGesendet: false,
          gueltigeErfasst: false,
          ungueltigeErfasst: false,
          kandidatenErfasst: false,
          stimmzettelgezaehlt: false,
        } as MBWState)
      );
    }
  }

  function getWahl(wahlID: string): Ref<WahlState> {
    let wahl = wahlStatus.value.get(wahlID);
    if (!wahl) {
      wahl = ref({
        niederschriftGedruckt: false,
        niederschriftGesendet: false,
        schnellmeldungGedruckt: false,
        schnellmeldungGesendet: false,
        gueltigeErfasst: false,
        ungueltigeErfasst: false,
        kandidatenErfasst: false,
        stimmzettelgezaehlt: false,
      } as MBWState);
      wahlStatus.value.set(wahlID, wahl);
    }
    return wahl;
  }

  return {
    status,
    isStatusSaving,
    isWahlvorstandErfasst,
    isWahlumgebungErfasst,
    wahlStatus,
    addWahl,
    getWahl,
    loadStatus,
    saveStatus,
  };
});

const { registerStoreHMR } = useHmrUpdate();
registerStoreHMR(useStatusStore);
