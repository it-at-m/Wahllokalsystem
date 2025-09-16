import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useOBWStapelBUtils(wahlID: ComputedRef<string>) {
  const { getErgebnisseByWahlIdAndStapelartOrUndefined } =
    useErgebnismeldungStore();

  const ergebnisseStapelBLeer = computed(() => {
    const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID.value,
      StapelArtEnum.ObwBLeer
    );
    return ergebnisseFound?.ergebnisse[0].ergebnis;
  });

  const ergebnisseStapelBUngekennzeichnet = computed(() => {
    const ergebnisseFound = getErgebnisseByWahlIdAndStapelartOrUndefined(
      wahlID.value,
      StapelArtEnum.ObwBUngekennzeichnet
    );
    return ergebnisseFound?.ergebnisse[0].ergebnis;
  });

  const sumStapelB = computed(
    () =>
      (ergebnisseStapelBUngekennzeichnet.value || 0) +
      (ergebnisseStapelBLeer.value || 0)
  );

  function createNewErgebnisseForStapelartObwB(
    stapelArt: StapelArtEnum,
    wahlbezirkID: string,
    ergebnisList: Ergebnis[]
  ): Ergebnisse {
    return {
      bezirkUndWahlIDStapelart: {
        wahlID: wahlID.value,
        wahlbezirkID: wahlbezirkID,
        stapelArt: stapelArt,
      },
      ergebnisse: ergebnisList,
    };
  }

  return {
    createNewErgebnisseForStapelartObwB,
    ergebnisseStapelBLeer,
    ergebnisseStapelBUngekennzeichnet,
    sumStapelB,
  };
}
