import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";
import type { Wahldaten } from "@/types/stimmabgabevermerke/Wahldaten.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export const useStimmabgabevermerkeStore = defineStore(
  "stimmabgabevermerke",
  () => {
    const stimmabgabevermerke = ref<Stimmabgabevermerke | null>(null);

    const lowestNumberOfRowsOverAllWahldaten = computed(() => {
      const wahldatenIterator = stimmabgabevermerke.value?.wahldaten.values();
      if (!wahldatenIterator) {
        return 0;
      }

      let minVermerkeLength = Infinity;
      for (const wahldaten of wahldatenIterator) {
        const currentVermerkeLength = wahldaten.vermerke.length;
        if (currentVermerkeLength < minVermerkeLength) {
          minVermerkeLength = currentVermerkeLength;
        }
      }

      return minVermerkeLength === Infinity ? 0 : minVermerkeLength;
    });

    const stimmabgabevermerkeTableTotalEachWahldaten = computed(() => {
      const totalsForWahldaten: number[] = [];

      stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
        const totalVermerke = wahldaten.vermerke.reduce((sum, vermerk) => {
          return sum + (vermerk.stimmzettel[0]?.anzahl ?? 0);
        }, 0);

        totalsForWahldaten.push(totalVermerke);
      });

      return totalsForWahldaten;
    });

    const sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl =
      computed(() => {
        const result = new Map<string, number>();
        stimmabgabevermerke.value?.wahldaten.forEach((wahldaten) => {
          let sumForWahl = 0;
          wahldaten.vermerke.forEach((vermerk) => {
            vermerk.stimmzettel.forEach((stimmzettel) => {
              sumForWahl += stimmzettel.anzahl ?? 0;
            });
          });
          sumForWahl +=
            wahldaten.eingenommeneWahlscheine.get(
              EingenommenerWahlscheinStimmzettelartEnum.Klein
            ) ?? 0;
          result.set(wahldaten.wahlID, sumForWahl);
        });
        return result;
      });

    function isAnyRowThatShouldBeDeletedFilled(newRowSize: number) {
      const currentLowestNumberOfRowsOverAllWahldaten =
        lowestNumberOfRowsOverAllWahldaten.value;
      const allVermerkeThatShouldBeRemoved: Vermerke[] = [];
      stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
        if (newRowSize > 0 && currentLowestNumberOfRowsOverAllWahldaten) {
          const removeRows =
            newRowSize - currentLowestNumberOfRowsOverAllWahldaten - 1;
          allVermerkeThatShouldBeRemoved.push(
            ...wahldaten.vermerke.slice(removeRows)
          );
        }
      });
      return allVermerkeThatShouldBeRemoved.some((vermerk) =>
        vermerk.stimmzettel.some(
          (stimmzettel) => stimmzettel.anzahl != null && stimmzettel.anzahl != 0
        )
      );
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

    function _increaseRows(newRowSize: number) {
      const currentLowestRows = lowestNumberOfRowsOverAllWahldaten.value;

      if (currentLowestRows !== null && currentLowestRows !== undefined) {
        stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
          for (
            let rowIndex = currentLowestRows;
            rowIndex < newRowSize - 1;
            rowIndex++
          ) {
            wahldaten.vermerke.push({
              blattnummer: rowIndex + 1,
              stimmzettel: [
                {
                  anzahl: null,
                  stimmzettelart: StimmzettelStimmzettelartEnum.Klein,
                },
              ],
            });
          }
        });
      }
    }

    function _decreaseRows(newRowSize: number) {
      const currentLowestNumberOfRowsOverAllWahldaten =
        lowestNumberOfRowsOverAllWahldaten.value;
      stimmabgabevermerke.value?.wahldaten.forEach((wahldaten: Wahldaten) => {
        if (newRowSize > 0 && currentLowestNumberOfRowsOverAllWahldaten) {
          const removeRows =
            newRowSize - currentLowestNumberOfRowsOverAllWahldaten - 1;
          wahldaten.vermerke.splice(removeRows, removeRows * -1);
        }
      });
    }

    return {
      stimmabgabevermerke,
      isAnyRowThatShouldBeDeletedFilled,
      stimmabgabevermerkeTableTotalEachWahldaten,
      lowestNumberOfRowsOverAllWahldaten,
      changeRowCount,
      sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl,
    };
  }
);
