import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

import { useLogging } from "@/composables/common/logging.ts";
import { useUserStore } from "@/stores/userStore.ts";

export function useTextFormatter() {
  function getStimmzettelTermForWahl(wahl: Wahl | undefined): string {
    const { logDebug } = useLogging("textFormatter");

    if (wahl) {
      return getStimmzettelTermForWahlID(wahl.wahlID);
    } else {
      logDebug("Wahl not found");
      return "";
    }
  }

  function getStimmzettelTermForWahlID(wahlId: string): string {
    const { isBWB, currentUserHauptWahlID } = storeToRefs(useUserStore());

    return isBWB.value && wahlId === currentUserHauptWahlID.value
      ? "Stimmzettel\u00adumschläge"
      : "Stimmzettel";
  }

  function getWahlscheineOrStimmabgabevermerkeTerm(): string {
    const { isUWB } = useUserStore();
    return isUWB ? "Stimmabgabevermerke" : "Wahlscheine";
  }

  function createTextVotes(count: number) {
    return `${count} ${createTextWithCorrectNumberTermStimme(count)}`;
  }

  function createTextInvalidVotes(count: number) {
    return `${count} ungültige ${createTextWithCorrectNumberTermStimme(count)}`;
  }

  function createTextWithCorrectNumberTermStimme(count: number) {
    return `${count > 1 ? "Stimmen" : "Stimme"}`;
  }

  return {
    createTextVotes,
    createTextInvalidVotes,
    createTextWithCorrectNumberTermStimme,
    getStimmzettelTermForWahl,
    getStimmzettelTermForWahlID,
    getWahlscheineOrStimmabgabevermerkeTerm,
  };
}
