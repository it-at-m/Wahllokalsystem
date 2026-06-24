import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKopfdatenService } from "@/composables/kopfdaten/kopfdatenService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { registerStoreHMR } = useHmrUpdate();
const kopfdatenService = useKopfdatenService();

export const useKopfdatenStore = defineStore("kopfdaten", () => {
  const kopfdaten = ref<Kopfdaten[]>([]);
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  async function initKopfdaten() {
    try {
      const loadedDataAsPromises = currentUserWahlMetadata.value.map(
        (metadata) =>
          kopfdatenService.getKopfdaten(metadata.wahlID, metadata.wahlbezirkID)
      );
      kopfdaten.value = await Promise.all(loadedDataAsPromises);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
    const wahlenStore = useWahlenStore();

    for (const kd of kopfdaten.value) {
      const wahl = wahlenStore.wahlenActions.getWahlOrUndefinedById(kd.wahlID);
      if (
        wahl &&
        wahl.wahlart === WahlWahlartEnum.Mbw &&
        (kd as Kopfdaten).maximalErlaubteStimmenProWaehler == null
      ) {
        throw Error(
          `Fehlende Angabe 'maximalErlaubteStimmenProWaehler' in Kopfdaten für MBW (wahlID=${kd.wahlID})`
        );
      }
    }
  }

  async function loadKopfdaten(
    wahlID: string,
    wahlbezirkID: string,
    sendNotification = true
  ) {
    try {
      const loadedKopfdaten = await kopfdatenService.getKopfdaten(
        wahlID,
        wahlbezirkID,
        sendNotification
      );
      kopfdaten.value.push(loadedKopfdaten);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
  }

  return {
    initKopfdaten,
    kopfdaten,
    loadKopfdaten,
  };
});

registerStoreHMR(useKopfdatenStore);
