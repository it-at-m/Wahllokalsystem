import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { Ref } from "vue";

import { computed } from "vue";

import { ZurueckweisungsgrundEnum } from "@/types/briefwahl/ZurueckweisungsgrundEnum.ts";

export function useBeanstandeteWahlbriefeGetter(
  wahlenState: Ref<{ wahlen: Wahl[] | null }>
) {
  const summeGueltigerWahlbriefe = computed(() => {
    if (!wahlenState.value.wahlen) return [];
    return wahlenState.value.wahlen.map(
      (wahl) =>
        wahl.beanstandeteWahlbriefe.filter(
          (brief) => brief === ZurueckweisungsgrundEnum.Zugelassen
        ).length
    );
  });

  const summeUngueltigerWahlbriefe = computed(() => {
    if (!wahlenState.value.wahlen) return [];
    return wahlenState.value.wahlen.map(
      (wahl) =>
        wahl.beanstandeteWahlbriefe.filter(
          (brief) =>
            brief !== ZurueckweisungsgrundEnum.Zugelassen && brief !== null
        ).length
    );
  });

  const summenZurueckweisungsgruende = computed(() => {
    if (!wahlenState.value.wahlen) return [];
    const anzahlWahlen = wahlenState.value.wahlen.length;
    const summenZurueckweisungsgruende = Object.values(ZurueckweisungsgrundEnum)
      .filter((grund) => grund !== ZurueckweisungsgrundEnum.Zugelassen)
      .map((grund) => ({
        summen: new Array(anzahlWahlen).fill(0),
        grund: grund,
      }));

    wahlenState.value.wahlen.forEach((wahl, wahlIndex) => {
      if (
        wahl.beanstandeteWahlbriefe &&
        wahl.beanstandeteWahlbriefe.every((grund) => grund !== null)
      ) {
        wahl.beanstandeteWahlbriefe.forEach((beanstandeterWahlbrief) => {
          if (beanstandeterWahlbrief !== ZurueckweisungsgrundEnum.Zugelassen) {
            const index = summenZurueckweisungsgruende.findIndex(
              (item) => item.grund === beanstandeterWahlbrief
            );
            summenZurueckweisungsgruende[index].summen[wahlIndex] += 1;
          }
        });
      }
    });
    return summenZurueckweisungsgruende;
  });

  return {
    summeGueltigerWahlbriefe,
    summeUngueltigerWahlbriefe,
    summenZurueckweisungsgruende,
  };
}
