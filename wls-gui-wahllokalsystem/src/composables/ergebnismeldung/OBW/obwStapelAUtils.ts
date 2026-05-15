import type { Ergebnis } from "@/types/ergebnismeldung/common/Ergebnis.ts";
import type { ErgebnisAndWahlvorschlag } from "@/types/ergebnismeldung/common/ErgebnisAndWahlvorschlag.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useErgebnisUtils } from "@/composables/ergebnismeldung/common/ergebnisUtils.ts";
import { useErgebnismeldungStore } from "@/stores/ergebnismeldungStore.ts";
import { useWahlvorschlaegeStore } from "@/stores/wahlvorschlaegeStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export function useOBWStapelAUtils(
  wahlID: ComputedRef<string>,
  wahlbezirkID: ComputedRef<string>
) {
  const STAPEL = StapelArtEnum.ObwA;

  const {
    getErgebnisseByWahlIdAndStapelartOrUndefined,
    getErgebnisseAndCreateIfMissing,
  } = useErgebnismeldungStore();

  const { orderedByNumIndexWithNullAtEnd } = useErgebnisUtils();

  const {
    getWahlvorschlagOrUndefinedByWahlIDWahlbezirkIDAndWahlvorschlagID,
    getWahlvorschlaegeByWahlIDAndWahlbezirkID,
  } = useWahlvorschlaegeStore();

  const { logWarn } = useLogging("obwUtilsStapelAUtils");

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

    const ergebnisseOfErgebnisse =
      getErgebnisseAndCreateIfMissing({
        wahlID: wahlID.value,
        wahlbezirkID: wahlbezirkID.value,
        stapelArt: STAPEL,
      })?.ergebnisse?.sort(orderedByNumIndexWithNullAtEnd) ?? [];

    ergebnisseOfErgebnisse.forEach((ergebnis) => {
      _addWahlvorschlagForErgebnisIfExisting(ergebnis, result);
    });

    if (result.length === 0) {
      const wahlvorschlaege = getWahlvorschlaegeByWahlIDAndWahlbezirkID(
        wahlID.value,
        wahlbezirkID.value
      );
      if (wahlvorschlaege) {
        [...wahlvorschlaege.wahlvorschlaege].forEach((wahlvorschlag, index) => {
          result.push({
            wahlvorschlag,
            ergebnis: {
              numIndex: index + 1,
              wahlvorschlagID: wahlvorschlag.identifikator,
              kandidatID: null,
              wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
              ergebnis: null,
            },
          });
        });
        const ergebnisse = getErgebnisseByWahlIdAndStapelartOrUndefined(
          wahlID.value,
          STAPEL
        );
        if (ergebnisse) {
          ergebnisse.ergebnisse = result.map(
            (ergebnisAndWahlvorschlag) => ergebnisAndWahlvorschlag.ergebnis
          );
        }
      }
    }
    return result;
  }

  return {
    ergebnisseAndWahlvorschlaege,
    sumOfValidVotes,
  };
}
