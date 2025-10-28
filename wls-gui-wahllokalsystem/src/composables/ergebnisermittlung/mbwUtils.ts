import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnisermittlung/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahlvorschlaege } from "@/types/wahlvorschlaege/Wahlvorschlaege.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

import { ref } from "vue";

import { useMbwErgebnisAndWahlvorschlagMapper } from "@/composables/ergebnisermittlung/mbwErgebnisAndWahlvorschlagMapper.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

const { postErgebnisse, getErgebnisse } = useErgebnisService();
const { getWahlvorschlaege } = useWahlvorschlaegeService();

export function useMbwUtils(wahlID: string, wahlbezirkID: string) {
  const { mapErgebnisseFromErgebnisseAndWahlvorschlagListToErgebnisse } =
    useMbwErgebnisAndWahlvorschlagMapper(wahlID, wahlbezirkID);

  const isErgebnisseSaving = ref<boolean>(false);

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

  function createEmptyErgebnisForWahlvorschlag(wahlvorschlag: Wahlvorschlag) {
    return {
      wahlvorschlagID: wahlvorschlag.identifikator,
      kandidatID: null,
      wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
      ergebnis: null,
      numIndex: null,
    };
  }

  async function loadWahlvorschlaege(wahlID: string, wahlbezirkID: string) {
    try {
      const loadedWahlvorschlaege = await getWahlvorschlaege(
        wahlID,
        wahlbezirkID
      );
      return _sortWahlvorschlaegeByOrdnungszahl(loadedWahlvorschlaege);
    } catch {
      throw new Error("Fehler beim Laden der Wahlvorschläge");
    }
  }

  async function loadAndCombineErgebnisseAndWahlvorschlaege() {
    const ergebnisse: MbwErgebnisseAndWahlvorschlag[] = [];

    const wahlvorschlaege = await loadWahlvorschlaege(wahlID, wahlbezirkID);
    const ergebnisseStapelA = await _loadGueltigeErgebnisseByStapelArt(
      wahlID,
      wahlbezirkID,
      StapelArtEnum.MbwA
    );
    const ergebnisseStapelB = await _loadGueltigeErgebnisseByStapelArt(
      wahlID,
      wahlbezirkID,
      StapelArtEnum.MbwB
    );

    for (const wahlvorschlag of wahlvorschlaege) {
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
          createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
        ergebnisStapelB:
          ergebnisStapelBForWahlvorschlag ??
          createEmptyErgebnisForWahlvorschlag(wahlvorschlag),
      });
    }
    return ergebnisse;
  }

  async function _loadGueltigeErgebnisseByStapelArt(
    wahlID: string,
    wahlbezirkID: string,
    stapelArt: StapelArtEnum
  ) {
    try {
      return await getErgebnisse(wahlbezirkID, wahlID, stapelArt, false);
    } catch {
      throw new Error("Fehler beim Laden der Ergebnisse");
    }
  }

  function _sortWahlvorschlaegeByOrdnungszahl(
    wahlvorschlaege: Wahlvorschlaege
  ) {
    const sortedArray = Array.from(wahlvorschlaege.wahlvorschlaege).sort(
      (vorschlagA, vorschlagB) =>
        vorschlagA.ordnungszahl - vorschlagB.ordnungszahl
    );
    return new Set(sortedArray);
  }

  return {
    isErgebnisseSaving,
    saveGueltigeErgebnisse,
    createEmptyErgebnisForWahlvorschlag,
    loadWahlvorschlaege,
    loadAndCombineErgebnisseAndWahlvorschlaege,
  };
}
