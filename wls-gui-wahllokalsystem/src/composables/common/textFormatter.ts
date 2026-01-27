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

  function createUuidv4() {
    return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(
      /[xy]/g,
      function (c) {
        const r = (Math.random() * 16) | 0;
        const v = c == "x" ? r : (r & 0x3) | 0x8;
        return v.toString(16);
      }
    );
  }

  return {
    getStimmzettelTermForWahl,
    createUuidv4,
  };
}
