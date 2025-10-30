import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

import { defineStore, storeToRefs } from "pinia";
import { computed, ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useStimmabgabevermerkeUtils } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export const useStimmabgabevermerkeStore = defineStore(
  "stimmabgabevermerke",
  () => {
    const stimmabgabevermerke = ref<Stimmabgabevermerke[]>([]);
    const isStimmabgabevermerkeSaving = ref(false);
    const { getStimmabgabevermerke, postStimmabgabevermerke } =
      useStimmabgabevermerkeService();
    const { createEmptyStimmabgabevermerke } = useStimmabgabevermerkeUtils();

    const { currentUserWahlMetadata } = storeToRefs(useUserStore());

    const { logDebug } = useLogging("stimmabgabevermerkeStore");

    const lowestNumberOfRowsOverAllWahldaten = computed(() => {
      if (
        !stimmabgabevermerke.value ||
        stimmabgabevermerke.value.length === 0
      ) {
        return 0;
      }

      let minVermerkeLength = Infinity;
      for (const stimmabgabevermerk of stimmabgabevermerke.value) {
        const currentVermerkeLength =
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          stimmabgabevermerk.wahldaten[0].vermerke.length;
        if (currentVermerkeLength < minVermerkeLength) {
          minVermerkeLength = currentVermerkeLength;
        }
      }

      return minVermerkeLength === Infinity ? 0 : minVermerkeLength;
    });

    const stimmabgabevermerkeTableTotalEachWahldaten = computed(() => {
      const totalsForStimmabgabevermerke: number[] = [];

      stimmabgabevermerke.value.forEach(
        (stimmabgabevermerk: Stimmabgabevermerke) => {
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          const totalVermerke = stimmabgabevermerk.wahldaten[0].vermerke.reduce(
            (sum, vermerk) => {
              let innerSum = 0;
              vermerk.stimmzettel.forEach((stimmzettel) => {
                if (
                  stimmzettel.stimmzettelart ==
                  StimmzettelStimmzettelartEnum.Klein
                ) {
                  innerSum = stimmzettel.anzahl ?? 0;
                }
              });
              return sum + innerSum;
            },
            0
          );
          totalsForStimmabgabevermerke.push(totalVermerke);
        }
      );

      return totalsForStimmabgabevermerke;
    });

    async function loadStimmabgabevermerke(
      wahlbezirkID: string,
      waehlerverzeichnisNummer: number,
      sendNotification = true
    ) {
      try {
        const loadedStimmabgabevermerke = await getStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer,
          sendNotification
        );

        if (loadedStimmabgabevermerke) {
          stimmabgabevermerke.value.push(loadedStimmabgabevermerke);
        } else {
          _addEmptyStimmabgabevermerke(wahlbezirkID, waehlerverzeichnisNummer);
        }
      } catch {
        throw Error(
          `Fehler beim laden des Stimmabgabevermerks mit wahlbezirkID ${wahlbezirkID} und waehlerverzeichnisNummer ${waehlerverzeichnisNummer}`
        );
      }
    }

    async function saveStimmabgabevermerke() {
      isStimmabgabevermerkeSaving.value = true;
      for (const stimmabgabevermerk of stimmabgabevermerke.value) {
        try {
          await postStimmabgabevermerke(
            stimmabgabevermerk.wahlbezirkID,
            stimmabgabevermerk.waehlerverzeichnisNummer,
            stimmabgabevermerk
          );
        } catch (e) {
          logDebug(
            `Save Stimmabgabevermerke for wahlbezirkID: ${stimmabgabevermerk.wahlbezirkID} and waehlerverzeichnisNummer: ${stimmabgabevermerk.waehlerverzeichnisNummer} failed`,
            e
          );
        } finally {
          isStimmabgabevermerkeSaving.value = false;
        }
      }
    }

    const sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl =
      computed(() => {
        const result = new Map<string, number>();
        stimmabgabevermerke.value.forEach((stimmabgabevermerk) => {
          let sumForWahl = 0;
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          stimmabgabevermerk.wahldaten[0].vermerke?.forEach((vermerk) => {
            vermerk.stimmzettel.forEach((stimmzettel) => {
              if (
                stimmzettel.stimmzettelart ==
                StimmzettelStimmzettelartEnum.Klein
              ) {
                sumForWahl += stimmzettel.anzahl ?? 0;
              }
            });
          });
          sumForWahl +=
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            stimmabgabevermerk.wahldaten[0].eingenommeneWahlscheine.get(
              EingenommenerWahlscheinStimmzettelartEnum.Klein
            ) ?? 0;
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          result.set(stimmabgabevermerk.wahldaten[0].wahlID, sumForWahl);
        });
        return result;
      });

    function isAnyRowThatShouldBeDeletedFilled(newRowSize: number) {
      const currentLowestNumberOfRowsOverAllWahldaten =
        lowestNumberOfRowsOverAllWahldaten.value;
      const allVermerkeThatShouldBeRemoved: Vermerke[] = [];
      stimmabgabevermerke.value.forEach(
        (stimmabgabevermerk: Stimmabgabevermerke) => {
          if (newRowSize > 0 && currentLowestNumberOfRowsOverAllWahldaten) {
            const removeRows =
              newRowSize - currentLowestNumberOfRowsOverAllWahldaten - 1;
            allVermerkeThatShouldBeRemoved.push(
              // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
              ...stimmabgabevermerk.wahldaten[0].vermerke.slice(removeRows)
            );
          }
        }
      );
      return allVermerkeThatShouldBeRemoved.some((vermerk) =>
        vermerk.stimmzettel.some(
          (stimmzettel) => stimmzettel.anzahl != null && stimmzettel.anzahl != 0
        )
      );
    }

    function getBlattnummernThatPreventDeletion(newRowSize: number) {
      const numbers: Set<number> = new Set<number>();
      const currentLowestNumberOfRowsOverAllWahldaten =
        lowestNumberOfRowsOverAllWahldaten.value;
      if (newRowSize > 0 && currentLowestNumberOfRowsOverAllWahldaten) {
        const removeRows =
          newRowSize - currentLowestNumberOfRowsOverAllWahldaten - 1;
        stimmabgabevermerke.value.forEach(
          (stimmabgabevermerk: Stimmabgabevermerke) => {
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            const vermerke = stimmabgabevermerk.wahldaten[0].vermerke;
            for (let i = removeRows; i < 0; i++) {
              const vermerk = vermerke[vermerke.length + i];
              if (
                vermerk &&
                vermerk.stimmzettel.some(
                  (stimmzettel) =>
                    stimmzettel.anzahl != null && stimmzettel.anzahl != 0
                )
              ) {
                numbers.add(vermerk.blattnummer);
              }
            }
          }
        );
      }
      return Array.from(numbers).sort((a, b) => a - b);
    }

    function changeRowCount(newRowSize: number) {
      if (lowestNumberOfRowsOverAllWahldaten.value != null) {
        if (newRowSize - 1 > lowestNumberOfRowsOverAllWahldaten.value) {
          _increaseRows(newRowSize);
        } else if (newRowSize - 1 < lowestNumberOfRowsOverAllWahldaten.value) {
          _decreaseRows(newRowSize);
        }
      }
    }

    function _addEmptyStimmabgabevermerke(
      wahlbezirkID: string,
      waehlerverzeichnisNummer: number
    ) {
      const wahlID = currentUserWahlMetadata.value.find(
        (m) => m.wahlbezirkID === wahlbezirkID
      )?.wahlID;
      if (wahlID !== undefined) {
        stimmabgabevermerke.value.push(
          createEmptyStimmabgabevermerke(
            wahlID,
            wahlbezirkID,
            waehlerverzeichnisNummer
          )
        );
      }
    }

    function _increaseRows(newRowSize: number) {
      const currentLowestRows = lowestNumberOfRowsOverAllWahldaten.value;

      if (currentLowestRows !== null && currentLowestRows !== undefined) {
        stimmabgabevermerke.value.forEach(
          (stimmabgabevermerk: Stimmabgabevermerke) => {
            for (
              let rowIndex = currentLowestRows;
              rowIndex < newRowSize - 1;
              rowIndex++
            ) {
              // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
              stimmabgabevermerk.wahldaten[0].vermerke.push({
                blattnummer: rowIndex + 2, //Vermerke starten mit der Blattnummer 2 und von da an weiter, da das erste Blatt die Beurkundung ist
                stimmzettel: [
                  {
                    anzahl: null,
                    stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                  },
                ],
              });
            }
          }
        );
      }
    }

    function _decreaseRows(newRowSize: number) {
      const currentLowestNumberOfRowsOverAllWahldaten =
        lowestNumberOfRowsOverAllWahldaten.value;
      stimmabgabevermerke.value?.forEach(
        (stimmabgabevermerk: Stimmabgabevermerke) => {
          if (newRowSize > 0 && currentLowestNumberOfRowsOverAllWahldaten) {
            const removeRows =
              newRowSize - currentLowestNumberOfRowsOverAllWahldaten - 1;
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            stimmabgabevermerk.wahldaten[0].vermerke.splice(
              removeRows,
              removeRows * -1
            );
          }
        }
      );
    }

    return {
      stimmabgabevermerke,
      isAnyRowThatShouldBeDeletedFilled,
      stimmabgabevermerkeTableTotalEachWahldaten,
      lowestNumberOfRowsOverAllWahldaten,
      getBlattnummernThatPreventDeletion,
      changeRowCount,
      sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl,
      loadStimmabgabevermerke,
      saveStimmabgabevermerke,
      isStimmabgabevermerkeSaving,
    };
  }
);
