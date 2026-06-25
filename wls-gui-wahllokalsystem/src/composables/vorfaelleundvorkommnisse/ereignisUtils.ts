import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";
import type { WahlbezirkEreignisse } from "@/types/vorfaelleundvorkommnisse/WahlbezirkEreignisse.ts";

import { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

export function useEreignisUtils() {
  function hasDoneVorkommnisse(ereignisse: WahlbezirkEreignisse | null) {
    return (
      ereignisse?.ereigniseintraege.some(
        (eintrag) => eintrag.ereignisart === EreignisartEnum.Vorkommnis
      ) || ereignisse?.keineVorkommnisse
    );
  }

  function compareEreignisseByUhrzeit(
    ereignis1: Ereignis,
    ereignis2: Ereignis
  ) {
    const time1 = ereignis1.uhrzeit
      ? new Date(ereignis1.uhrzeit).getTime()
      : Infinity;
    const time2 = ereignis2.uhrzeit
      ? new Date(ereignis2.uhrzeit).getTime()
      : Infinity;

    return time1 - time2;
  }

  return {
    hasDoneVorkommnisse,
    compareEreignisseByUhrzeit,
  };
}
