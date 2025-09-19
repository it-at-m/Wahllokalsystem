import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

type WahlvorschlagID = string;
type ErgebnisSum = number;

export function useOBWStapelCUtils(
  wahlID: ComputedRef<string>,
  wahlbezirkID: ComputedRef<string>
) {
  const {
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    switchStapelOfErgebnis,
  } = useErgebnismeldungStore();
  const { getWahlvorschlaegeByWahlIDAndWahlbezirkID } =
    useWahlvorschlaegeStore();

  const stapelCUngueltigErgebnisse: ComputedRef<ErgebnisAndStapelArt[]> =
    computed(
      () =>
        getErgebnisseByWahlIdAndStapelartOrUndefined(
          wahlID.value,
          StapelArtEnum.ObwCUngueltig
        )?.ergebnisse.map((ergebnis) => ({
          ergebnis: ergebnis,
          stapelArt: StapelArtEnum.ObwCUngueltig,
        })) ?? []
    );
  const stapelCUngueltigErgebnisseSum = computed(() =>
    stapelCUngueltigErgebnisse.value
      .map((ergebnisseAndStapelArt) => ergebnisseAndStapelArt.ergebnis)
      .reduce((sum, ergebnis) => sum + (ergebnis.ergebnis ?? 0), 0)
  );

  const stapelCGueltigErgebnisse: ComputedRef<ErgebnisAndStapelArt[]> =
    computed(
      () =>
        getErgebnisseByWahlIdAndStapelartOrUndefined(
          wahlID.value,
          StapelArtEnum.ObwCGueltig
        )?.ergebnisse.map((ergebnis) => ({
          ergebnis: ergebnis,
          stapelArt: StapelArtEnum.ObwCGueltig,
        })) ?? []
    );
  const stapelCGueltigErgebnisseSums = computed(() =>
    stapelCGueltigErgebnisse.value
      .map((ergebnisseAndStapelArt) => ergebnisseAndStapelArt.ergebnis)
      .reduce(
        (sumOfWahlvorschlag: Map<WahlvorschlagID, ErgebnisSum>, ergebnis) => {
          if (ergebnis.wahlvorschlagID !== null && ergebnis.ergebnis !== null) {
            const currentSum =
              sumOfWahlvorschlag.get(ergebnis.wahlvorschlagID) || 0;
            sumOfWahlvorschlag.set(
              ergebnis.wahlvorschlagID,
              currentSum + ergebnis.ergebnis
            );
          }
          return sumOfWahlvorschlag;
        },
        new Map<WahlvorschlagID, ErgebnisSum>()
      )
  );

  const wahlvorschlaegeAndSumAboveZero = computed(() =>
    wahlvorschlaege.value
      .filter((wahlvorschlag) =>
        stapelCGueltigErgebnisseSums.value.has(wahlvorschlag.identifikator)
      )
      .map((wahlvorschlag) => ({
        wahlvorschlag,
        sum:
          stapelCGueltigErgebnisseSums.value.get(wahlvorschlag.identifikator) ||
          0,
      }))
  );

  const totalSum = computed(
    () =>
      stapelCUngueltigErgebnisseSum.value +
      [...stapelCGueltigErgebnisseSums.value.values()].reduce(
        (sum, currentValue) => sum + currentValue,
        0
      )
  );

  const wahlvorschlaege = computed(() => {
    const wahlvorschlaege = getWahlvorschlaegeByWahlIDAndWahlbezirkID(
      wahlID.value,
      wahlbezirkID.value
    );
    return wahlvorschlaege ? [...(wahlvorschlaege.wahlvorschlaege ?? [])] : [];
  });

  function switchStapelCOfErgebnis(
    currentErgebnisAndStapel: {
      stapelArt: StapelArtEnum;
      ergebnis: {
        numIndex: number;
      };
    },
    shouldSetStapelUngueltig: boolean
  ) {
    const newStapelArt = shouldSetStapelUngueltig
      ? StapelArtEnum.ObwCUngueltig
      : StapelArtEnum.ObwCGueltig;
    switchStapelOfErgebnis(
      {
        wahlID: wahlID.value,
        wahlbezirkID: wahlbezirkID.value,
        stapelArt: currentErgebnisAndStapel.stapelArt,
      },
      currentErgebnisAndStapel.ergebnis.numIndex,
      newStapelArt
    );
  }

  return {
    stapelCUngueltigErgebnisse,
    stapelCUngueltigErgebnisseSum,
    stapelCGueltigErgebnisse,
    stapelCGueltigErgebnisseSums,
    wahlvorschlaege,
    wahlvorschlaegeAndSumAboveZero,
    totalSum,
    switchStapelCOfErgebnis,
  };
}
