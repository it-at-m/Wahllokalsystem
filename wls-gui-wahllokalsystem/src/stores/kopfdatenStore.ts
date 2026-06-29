import type { Kopfdaten } from "@/types/kopfdaten/kopfdaten.ts";

import { defineStore, storeToRefs } from "pinia";
import { ref } from "vue";

import { useHmrUpdate } from "@/composables/common/hmrUpdate.ts";
import { useKopfdatenService } from "@/composables/kopfdaten/kopfdatenService.ts";
import { useUserNotificationService } from "@/composables/userNotification/userNotificationService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { UserNotificationCategoryEnum } from "@/types/userNotification/UserNotificationCategoryEnum.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const { registerStoreHMR } = useHmrUpdate();
const kopfdatenService = useKopfdatenService();
const userNotificationService = useUserNotificationService();

export const useKopfdatenStore = defineStore("kopfdaten", () => {
  const kopfdaten = ref<Kopfdaten[]>([]);
  const { currentUserWahlMetadata } = storeToRefs(useUserStore());

  async function initKopfdaten(sendNotification = true) {
    try {
      const loadedDataAsPromises = currentUserWahlMetadata.value.map(
        (metadata) =>
          kopfdatenService.getKopfdaten(
            metadata.wahlID,
            metadata.wahlbezirkID,
            sendNotification
          )
      );
      kopfdaten.value = await Promise.all(loadedDataAsPromises);
    } catch {
      throw Error("Fehler beim Resolven der Promises");
    }
    const wahlenStore = useWahlenStore();

    for (const kd of kopfdaten.value) {
      const wahl = wahlenStore.wahlenActions.getWahlOrUndefinedById(kd.wahlID);
      if (
        wahl?.wahlart === WahlWahlartEnum.Mbw &&
        kd.maximalErlaubteStimmenProWaehler == null
      ) {
        if (sendNotification) {
          userNotificationService.addNotification(
            `Fehler: Erlaubte Stimmen pro Wähler für Wahl oder Wahlart konnten nicht geladen werden.`,
            UserNotificationCategoryEnum.ERROR
          );
        }
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
