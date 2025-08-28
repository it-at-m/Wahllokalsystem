import type { Stimmabgabevermerke } from "@/types/stimmabgabevermerke/Stimmabgabevermerke.ts";
import type { Vermerke } from "@/types/stimmabgabevermerke/Vermerke.ts";

import { defineStore } from "pinia";
import { computed, ref } from "vue";

import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";
import { StimmzettelStimmzettelartEnum } from "@/types/stimmabgabevermerke/StimmzettelStimmzettelartEnum.ts";

export const useStimmabgabevermerkeStore = defineStore(
  "stimmabgabevermerke",
  () => {
    const stimmabgabevermerke = ref<Stimmabgabevermerke[]>([]);
    const { getStimmabgabevermerke } = useStimmabgabevermerkeService();

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
          const totalVermerke = stimmabgabevermerk.wahldaten[0].vermerke.reduce(
            (sum, vermerk) => {
              return sum + (vermerk.stimmzettel[0]?.anzahl ?? 0);
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
      waehlerverzeichnisNummer: number
    ) {
      try {
        const loadedStimmabgabevermerke = await getStimmabgabevermerke(
          wahlbezirkID,
          waehlerverzeichnisNummer
        );
        stimmabgabevermerke.value.push(loadedStimmabgabevermerke);
      } catch {
        throw Error(
          `Fehler beim laden des Stimmabgabevermerks mit wahlbezirkID ${wahlbezirkID} und waehlerverzeichnisNummer ${waehlerverzeichnisNummer}`
        );
      }
    }

    const sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl =
      computed(() => {
        const result = new Map<string, number>();
        stimmabgabevermerke.value.forEach((stimmabgabevermerke) => {
          let sumForWahl = 0;
          stimmabgabevermerke.wahldaten[0].vermerke?.forEach((vermerk) => {
            vermerk.stimmzettel.forEach((stimmzettel) => {
              sumForWahl += stimmzettel.anzahl ?? 0;
            });
          });
          sumForWahl +=
            stimmabgabevermerke.wahldaten[0].eingenommeneWahlscheine.get(
              EingenommenerWahlscheinStimmzettelartEnum.Klein
            ) ?? 0;
          result.set(stimmabgabevermerke.wahldaten[0].wahlID, sumForWahl);
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
        stimmabgabevermerke.value.forEach(
          (stimmabgabevermerk: Stimmabgabevermerke) => {
            for (
              let rowIndex = currentLowestRows;
              rowIndex < newRowSize - 1;
              rowIndex++
            ) {
              stimmabgabevermerk.wahldaten[0].vermerke.push({
                blattnummer: rowIndex + 1,
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
      changeRowCount,
      sumEingenommeneWahlscheineAndStimmabgabevermerkeForEachWahl,
      loadStimmabgabevermerke,
    };
  }
);
