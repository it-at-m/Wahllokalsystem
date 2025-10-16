import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

import { useLogging } from "@/composables/common/logging.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useTextFormatter() {
  function getStimmzettelTermForWahl(wahl: Wahl | undefined): string {
    const { isBWB } = storeToRefs(useUserStore());
    const { wahlenActions } = useWahlenStore();
    const { logDebug } = useLogging("textFormatter");

    if (wahl) {
      return isBWB.value && wahlenActions.isWahlWithSmallestWahlnummer(wahl)
        ? "Stimmzettelumschläge"
        : "Stimmzettel";
    } else {
      logDebug("Wahl not found");
      return "";
    }
  }

  return {
    getStimmzettelTermForWahl,
  };
}
