import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { useErgebnisTools } from "@/composables/ergebnismeldung/ergebnisTools.ts";

export function useMbwErgebnisseAndWahlvorschlagTools() {
  const { createWithErgebnisOnly } = useErgebnisTools();

  function createWithErgebnissen(
    wahlvorschlag: Wahlvorschlag,
    ergebnisStapelA: number,
    ergebnisStapelB: number
  ): MbwErgebnisseAndWahlvorschlag {
    return {
      wahlvorschlag: wahlvorschlag,
      ergebnisStapelA: createWithErgebnisOnly(ergebnisStapelA),
      ergebnisStapelB: createWithErgebnisOnly(ergebnisStapelB),
    };
  }

  return { createWithErgebnissen };
}
