import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";

import { storeToRefs } from "pinia";

import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useBWerteService(wahlID: string, wahlbezirkID: string) {
  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const { isUWB, isBWB } = storeToRefs(useUserStore());

  const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
  const { getStimmzettelumschlaege } = useErgebnisService();

  async function getBWerteForWahlbezirkAndWahl(): Promise<BWerte> {
    const bWerte: BWerte = {
      bezirkUndWahlID: {
        wahlbezirkID: wahlbezirkID,
        wahlID: wahlID,
      },
      b: 0,
      b1: 0,
      b2: 0,
    };

    try {
      if (isUWB.value) {
        const waehlerverzeichnisNummer =
          waehlerverzeichnisActions.getWaehlerverzeichnisNummerOrUndefinedById(
            wahlID
          );
        if (waehlerverzeichnisNummer) {
          const loadedStimmabgabevermerke = await getStimmabgabevermerke(
            wahlbezirkID,
            wahlID,
            waehlerverzeichnisNummer
          );
          if (loadedStimmabgabevermerke) {
            bWerte.b1 = loadedStimmabgabevermerke.vermerke
              .flatMap((vermerk) => vermerk.stimmzettel)
              .reduce(
                (summe, stimmzettel) => summe + (stimmzettel.anzahl || 0),
                0
              );
            bWerte.b2 = Array.from(
              loadedStimmabgabevermerke.eingenommeneWahlscheine.values()
            ).reduce((sum, value) => sum + value, 0);

            bWerte.b = bWerte.b1 + bWerte.b2;
          }
        }
      }
      if (isBWB.value) {
        const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
        if (wahl) {
          const loadedStimmzettelumschlaege = await getStimmzettelumschlaege(
            wahl,
            wahlbezirkID,
            "",
            false
          );
          bWerte.b = loadedStimmzettelumschlaege?.anzahlWaehler || 0;
        }
      }
    } catch {
      throw new Error(`Fehler beim Laden der BWerte`);
    }

    return bWerte;
  }

  return {
    getBWerteForWahlbezirkAndWahl,
  };
}
