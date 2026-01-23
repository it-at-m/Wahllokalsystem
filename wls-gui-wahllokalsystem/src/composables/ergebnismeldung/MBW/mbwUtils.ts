import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useAWerteService } from "@/composables/ergebnismeldung/common/aWerteService.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useMbwErgebnisAndWahlvorschlagMapper } from "@/composables/ergebnismeldung/MBW/mbwErgebnisAndWahlvorschlagMapper.ts";
import { useStimmabgabevermerkeService } from "@/composables/stimmabgabevermerke/stimmabgabevermerkeService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

const {
  postErgebnisse,
  getErgebnisse,
  postSchnellmeldung,
  getStimmzettelumschlaege,
} = useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();
const { sortWahlvorschlaegeByOrdnungszahl } = useWahlvorschlagUtils();
const { getAWerte } = useAWerteService();
const { getStimmabgabevermerke } = useStimmabgabevermerkeService();
const { logError } = useLogging("mbwUtils");

export function useMbwUtils(wahlID: string, wahlbezirkID: string) {
  const { mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse } =
    useMbwErgebnisAndWahlvorschlagMapper(wahlID, wahlbezirkID);

  const { currentUserWahlbezirkID } = storeToRefs(useUserStore());
  const { wahlenActions, waehlerverzeichnisActions } = useWahlenStore();
  const { isUWB, isBWB } = storeToRefs(useUserStore());

  const isErgebnisseSaving = ref<boolean>(false);
  const isSendingSchnellmeldung = ref<boolean>(false);

  async function saveGueltigeErgebnisse(
    ergebnisse: MbwErgebnisseAndWahlvorschlag[]
  ) {
    isErgebnisseSaving.value = true;

    try {
      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwA,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwA,
          ergebnisse
        ),
        true
      );

      await postErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArtEnum.MbwB,
        mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse(
          StapelArtEnum.MbwB,
          ergebnisse
        ),
        true
      );
    } catch {
      throw new Error("Fehler beim Speichern der Ergebnisse");
    } finally {
      isErgebnisseSaving.value = false;
    }
  }

  async function loadAndCombineErgebnisseAndWahlvorschlaege() {
    const ergebnisse: MbwErgebnisseAndWahlvorschlag[] = [];

    const wahlvorschlaege = await _loadWahlvorschlaege();
    const ergebnisseStapelA = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwA
    );
    const ergebnisseStapelB = await _loadGueltigeErgebnisseByStapelArt(
      StapelArtEnum.MbwB
    );

    for (const wahlvorschlag of wahlvorschlaege.wahlvorschlaege) {
      const ergebnisStapelAForWahlvorschlag =
        ergebnisseStapelA?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );
      const ergebnisStapelBForWahlvorschlag =
        ergebnisseStapelB?.ergebnisse.find(
          (ergebnis) => ergebnis.wahlvorschlagID === wahlvorschlag.identifikator
        );

      ergebnisse.push({
        wahlvorschlag: wahlvorschlag,
        ergebnisStapelA:
          ergebnisStapelAForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
        ergebnisStapelB:
          ergebnisStapelBForWahlvorschlag ??
          _createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
      });
    }
    return ergebnisse;
  }

  async function getAWerteForWahlbezirkAndWahl(): Promise<AWerte> {
    let aWerte;
    try {
      aWerte = await getAWerte(wahlbezirkID, false);
    } catch {
      throw new Error(`Fehler beim Laden der AWerte`);
    }

    const filteredAWert = aWerte.find(
      ({ bezirkUndWahlID }) => bezirkUndWahlID.wahlID === wahlID
    );

    if (!filteredAWert) {
      throw new Error(`Kein AWert gefunden für wahlID: ${wahlID}`);
    }
    return filteredAWert;
  }

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
            waehlerverzeichnisNummer
          );
          // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
          bWerte.b1 = loadedStimmabgabevermerke.wahldaten[0].vermerke
            .flatMap((vermerk) => vermerk.stimmzettel)
            .reduce(
              (summe, stimmzettel) => summe + (stimmzettel.anzahl || 0),
              0
            );
          bWerte.b2 = Array.from(
            // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
            loadedStimmabgabevermerke.wahldaten[0].eingenommeneWahlscheine.values()
          ).reduce((sum, value) => sum + value, 0);

          bWerte.b = bWerte.b1 + bWerte.b2;
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

  async function sendSchnellmeldung() {
    isSendingSchnellmeldung.value = true;

    try {
      const wahl = wahlenActions.getWahlOrUndefinedById(wahlID);
      if (!wahl) {
        logError(`zur wahlID ${wahlID} existiert keine Wahl`);
      } else {
        await postSchnellmeldung(
          wahlID,
          wahlbezirkID,
          currentUserWahlbezirkID.value,
          wahl.waehlerverzeichnisNummer
        );
      }
    } finally {
      isSendingSchnellmeldung.value = false;
    }
  }

  async function _loadGueltigeErgebnisseByStapelArt(stapelArt: StapelArtEnum) {
    try {
      return await getErgebnisse(wahlbezirkID, wahlID, stapelArt, false);
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  async function _loadWahlvorschlaege() {
    try {
      const loadedWahlvorschlaege = await getWahlvorschlaege(
        wahlID,
        wahlbezirkID
      );
      return sortWahlvorschlaegeByOrdnungszahl(loadedWahlvorschlaege);
    } catch {
      throw new Error("Fehler beim Laden der Wahlvorschläge");
    }
  }

  function _createEmptyErgebnisForWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
      ergebnis: null,
      numIndex: null,
    };
  }

  return {
    isErgebnisseSaving,
    isSendingSchnellmeldung,
    saveGueltigeErgebnisse,
    loadAndCombineErgebnisseAndWahlvorschlaege,
    getAWerteForWahlbezirkAndWahl,
    getBWerteForWahlbezirkAndWahl,
    sendSchnellmeldung,
  };
}
