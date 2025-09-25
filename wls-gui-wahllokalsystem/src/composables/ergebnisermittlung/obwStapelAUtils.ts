import type { ErgebnisAndWahlvorschlag } from "@/types/ergebnisermittlung/ErgebnisAndWahlvorschlag.ts";
import type { Ergebnis } from "@/types/ergebnismeldung/Ergebnis.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useOBWStapelCUtils } from "@/composables/ergebnisermittlung/obwStapelCUtils.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useOBWStapelAUtils(
  wahlID: ComputedRef<string>,
  wahlbezirkID: ComputedRef<string>
) {
  const STAPEL = StapelArtEnum.ObwA;

  const { stapelCGueltigErgebnisse } = useOBWStapelCUtils(wahlID, wahlbezirkID);
  const { getErgebnisseByWahlIdAndStapelartOrUndefined } =
    useErgebnismeldungStore();
  const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();
  const { getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID } =
    useWahlvorschlaegeStore();

  const { logWarn } = useLogging("obwUtils");

  const ergebnisseAndWahlvorschlaege = computed<ErgebnisAndWahlvorschlag[]>(
    () => {
      return _createErgebnisseAndWahlvorschlaege();
    }
  );

  const sumOfValidVotes = computed(() =>
    ergebnisseAndWahlvorschlaege.value
      .map((item) => item.ergebnis)
      .reduce((sum, ergebnis) => {
        return sum + (ergebnis.ergebnis ?? 0);
      }, 0)
  );

  function getErgebnisStapelCByWahlvorschlagIdOrZero(
    wahlvorschlagID: string | null
  ): number | null {
    const foundItem = stapelCGueltigErgebnisse.value.find(
      (item) => item.ergebnis.wahlvorschlagID === wahlvorschlagID
    );
    return foundItem ? foundItem.ergebnis.ergebnis : 0;
  }

  function _addWahlvorschlagForErgebnisIfExisting(
    ergebnis: Ergebnis,
    result: ErgebnisAndWahlvorschlag[]
  ): void {
    if (ergebnis.wahlvorschlagID) {
      const wahlvorschlagForErgebnis =
        getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID(
          wahlID.value,
          wahlbezirkID.value,
          ergebnis.wahlvorschlagID
        );
      if (wahlvorschlagForErgebnis) {
        result.push({
          ergebnis: ergebnis,
          wahlvorschlag: wahlvorschlagForErgebnis,
        });
      } else {
        logWarn(
          `ergebnis wahlID=${wahlID.value}, wahlbezirkID=${wahlbezirkID.value}, wahlvorschlagID=${ergebnis.wahlvorschlagID} hat keinen Wahlvorschlag`
        );
      }
    } else {
      logWarn(
        `ergebnis wahlID=${wahlID.value}, wahlbezirkID=${wahlbezirkID.value} ist ohne wahlvorschlagID`
      );
    }
  }

  function _createErgebnisseAndWahlvorschlaege() {
    const result: ErgebnisAndWahlvorschlag[] = [];

    const ergebnisseForWahlAndStapel =
      getErgebnisseByWahlIdAndStapelartOrUndefined(
        wahlID.value,
        STAPEL
      )?.ergebnisse?.sort(orderedByNumIndexWithNullAtEnd) ?? [];

    ergebnisseForWahlAndStapel.forEach((ergebnis) => {
      _addWahlvorschlagForErgebnisIfExisting(ergebnis, result);
    });

    return result;
  }

  return {
    ergebnisseAndWahlvorschlaege,
    sumOfValidVotes,
    getErgebnisStapelCByWahlvorschlagIdOrZero,
  };
}
