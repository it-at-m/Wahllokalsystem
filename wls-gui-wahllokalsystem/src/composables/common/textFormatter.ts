import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

import { useLogging } from "@/composables/common/logging.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useTextFormatter() {
  function getStimmzettelTermForWahl(wahl: Wahl | undefined): string {
    const { isBWB, currentUserHauptWahlID } = storeToRefs(useUserStore());
    const { logDebug } = useLogging("textFormatter");

    if (wahl) {
      return isBWB.value && wahl.wahlID === currentUserHauptWahlID.value
        ? "Stimmzettelumschläge"
        : "Stimmzettel";
    } else {
      logDebug("Wahl not found");
      return "";
    }
  }

  function getStimmzettelTermForWahlID(wahlId: string): string {
    const { isBWB, currentUserHauptWahlID } = storeToRefs(useUserStore());

    return isBWB.value && wahlId === currentUserHauptWahlID.value
      ? "Stimmzettelumschläge"
      : "Stimmzettel";
  }

  return {
    getStimmzettelTermForWahl,
    getStimmzettelTermForWahlID,
  };
}
