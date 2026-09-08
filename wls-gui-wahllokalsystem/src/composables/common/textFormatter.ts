import type { Wahl } from "@/types/wahl/Wahl.ts";

import { storeToRefs } from "pinia";

import { useLogging } from "@/composables/common/logging.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { SystemBeschlussgrundReasonEnum } from "@/types/dse/beschlussfassung/SystemBeschlussgrundReasonEnum.ts";

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
    return `${Math.abs(count) === 1 ? "Stimme" : "Stimmen"}`;
  }

  function mapSystemBeschlussgrundText(text: string): string {
    switch (text) {
      case SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenAberImGesamtstimmenlimit:
        return "Zu viele Einzelstimmen, aber im Gesamtstimmenlimit";
      case SystemBeschlussgrundReasonEnum.KeineReststimmenvergabeMoeglich:
        return "Keine Reststimmenvergabe möglich";
      case SystemBeschlussgrundReasonEnum.EinzelneStimmenUngueltig:
        return "Einzelne Stimmen ungültig";
      case SystemBeschlussgrundReasonEnum.ZuVieleEinzelstimmenOderListenkreuze:
        return "Zu viele Einzelstimmen oder Listenkreuze";
      default:
        return text;
    }
  }

  return {
    createTextVotes,
    createTextInvalidVotes,
    createTextWithCorrectNumberTermStimme,
    getStimmzettelTermForWahl,
    getStimmzettelTermForWahlID,
    getWahlscheineOrStimmabgabevermerkeTerm,
    mapSystemBeschlussgrundText,
  };
}
