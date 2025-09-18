import type { ErgebnisAndStapelArt } from "@/types/ergebnisermittlung/ErgebnisAndStapelArt.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useMathUtils } from "@/composables/common/mathUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useOBWStapelCUtils(
  wahlID: ComputedRef<string>,
  wahlbezirkID: ComputedRef<string>
) {
  const {
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    getErgebnisseAndCreateIfMissing,
    switchStapelOfErgebnis,
  } = useErgebnismeldungStore();
  const { getWahlvorschlaegeByWahlIDAndWahlbezirkID } =
    useWahlvorschlaegeStore();
  const { reduceToMaxOfNumIndex } = useErgebnisUtils();
  const { maxOfOptionalNumbers } = useMathUtils();

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
      .reduce((sumOfWahlvorschlag: Map<string, number>, ergebnis) => {
        if (ergebnis.wahlvorschlagID !== null && ergebnis.ergebnis !== null) {
          const currentSum =
            sumOfWahlvorschlag.get(ergebnis.wahlvorschlagID) || 0;
          sumOfWahlvorschlag.set(
            ergebnis.wahlvorschlagID,
            currentSum + ergebnis.ergebnis
          );
        }
        return sumOfWahlvorschlag;
      }, new Map<string, number>())
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

  function addGueltigErgebnisse(newAmount: number) {
    const ergebnisseForAdding = getErgebnisseAndCreateIfMissing({
      wahlID: wahlID.value,
      wahlbezirkID: wahlbezirkID.value,
      stapelArt: StapelArtEnum.ObwCGueltig,
    });
    const lastUsedNumIndex = getMaxNumIndex() ?? 0;
    const itemsToAdd: Ergebnis[] = [];
    for (let i = 0; i < newAmount - lastUsedNumIndex; i++) {
      itemsToAdd.push({
        numIndex: lastUsedNumIndex + i + 1,
        wahlvorschlagID: null,
        ergebnis: 1,
        kandidatID: null,
        wahlvorschlagsOrdnungszahl: null,
      });
    }
    ergebnisseForAdding.ergebnisse.push(...itemsToAdd);
  }

  function deleteErgebnisseWithNumIndexAbove(numIndex: number) {
    const ergebnisseGueltigToUpdate = getErgebnisseAndCreateIfMissing({
      wahlID: wahlID.value,
      wahlbezirkID: wahlbezirkID.value,
      stapelArt: StapelArtEnum.ObwCGueltig,
    });
    const gueltigeToKeep = ergebnisseGueltigToUpdate.ergebnisse.filter(
      (ergebnis) => (ergebnis.numIndex || 0) <= numIndex
    );
    ergebnisseGueltigToUpdate.ergebnisse = gueltigeToKeep;

    const ergebnisseUngueltigToUpdate = getErgebnisseAndCreateIfMissing({
      wahlID: wahlID.value,
      wahlbezirkID: wahlbezirkID.value,
      stapelArt: StapelArtEnum.ObwCUngueltig,
    });
    const ungueltigeToKeep = ergebnisseUngueltigToUpdate.ergebnisse.filter(
      (ergebnis) => (ergebnis.numIndex || 0) <= numIndex
    );
    ergebnisseUngueltigToUpdate.ergebnisse = ungueltigeToKeep;
  }

  function getMaxNumIndex() {
    const maxNumIndexOfCUngueltig = _getMaxNumIndexOfCUngueltig();
    const maxNumIndexOfCGueltig = _getMaxNumIndexOfCGueltig();
    return maxOfOptionalNumbers([
      maxNumIndexOfCGueltig,
      maxNumIndexOfCUngueltig,
    ]);
  }

  function getMaxNumIndexWithValueSet() {
    const maxNumIndexOfCUngueltig = _getMaxNumIndexOfCUngueltig();
    const maxNumIndexOfCGueltig = _getMaxNumIndexOfUsedCGueltig();
    return maxOfOptionalNumbers([
      maxNumIndexOfCGueltig,
      maxNumIndexOfCUngueltig,
    ]);
  }

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

  function _getMaxNumIndexOfCUngueltig() {
    return stapelCUngueltigErgebnisse.value
      .map((ergebnisseAndStapelArt) => ergebnisseAndStapelArt.ergebnis)
      .reduce(reduceToMaxOfNumIndex, null);
  }

  function _getMaxNumIndexOfCGueltig() {
    return stapelCGueltigErgebnisse.value
      .map((ergebnisseAndStapelArt) => ergebnisseAndStapelArt.ergebnis)
      .reduce(reduceToMaxOfNumIndex, null);
  }

  function _getMaxNumIndexOfUsedCGueltig() {
    return stapelCGueltigErgebnisse.value
      .map((ergebnisseAndStapelArt) => ergebnisseAndStapelArt.ergebnis)
      .filter((ergebnis) => ergebnis.wahlvorschlagID !== null) //nur die beachten die auch gepflegt wurden
      .reduce(reduceToMaxOfNumIndex, null);
  }

  return {
    stapelCUngueltigErgebnisse,
    stapelCUngueltigErgebnisseSum,
    stapelCGueltigErgebnisse,
    stapelCGueltigErgebnisseSums,
    wahlvorschlaege,
    wahlvorschlaegeAndSumAboveZero,
    totalSum,
    addGueltigErgebnisse,
    deleteErgebnisseWithNumIndexAbove,
    getMaxNumIndex,
    getMaxNumIndexWithValueSet,
    switchStapelCOfErgebnis,
  };
}
