import type { WahlvorschlagWithScorableKandidaten } from "@/types/ergebnisermittlung/WahlvorschlagWithScorableKandidaten.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Ref } from "vue";

import { ref } from "vue";

import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useMwbStapelBCUtils(wahlbezirkID: string, wahlID: string) {
  const StapelArt_BC = StapelArtEnum.MbwBC;

  const { getErgebnisse, postErgebnisse } = useErgebnisService();
  const { getWahlvorschlaege } = useWahlvorschlaegeService();
  const { sortWahlvorschlaegeByOrdnungszahl } = useWahlvorschlagUtils();

  const isLoading = ref(false);
  const isSaving = ref(false);
  const scorableWahlvorschlaege: Ref<WahlvorschlagWithScorableKandidaten[]> =
    ref([]);

  async function loadWahlvorschlaegeAndErgebnisse() {
    isLoading.value = true;
    try {
      const wahlvorschlaege = await getWahlvorschlaege(wahlID, wahlbezirkID);
      sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege);
      const ergebnisse = await getErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArt_BC,
        false
      );

      const result: WahlvorschlagWithScorableKandidaten[] = [];

      for (const wahlvorschlag of wahlvorschlaege.wahlvorschlaege) {
        console.log(`wahlvorschlagDTO > `, wahlvorschlag);
        const wahlvorschlagWithScorableKandidaten: WahlvorschlagWithScorableKandidaten =
          {
            identifikator: wahlvorschlag.identifikator,
            scorableKandidaten: [],
            kurzname: wahlvorschlag.kurzname,
            ordnungszahl: wahlvorschlag.ordnungszahl,
          };
        console.log(`wahlvorschlag > `, wahlvorschlagWithScorableKandidaten);
        result.push(wahlvorschlagWithScorableKandidaten);
        if (wahlvorschlag.kandidaten) {
          for (const kandidat of wahlvorschlag.kandidaten) {
            const savedErgebnisForKandidat = ergebnisse?.ergebnisse.find(
              (ergebnis) => ergebnis.kandidatID === kandidat.identifikator
            );
            if (savedErgebnisForKandidat) {
              wahlvorschlagWithScorableKandidaten.scorableKandidaten.push({
                ergebnis: savedErgebnisForKandidat,
                kandidat,
              });
            } else {
              wahlvorschlagWithScorableKandidaten.scorableKandidaten.push({
                ergebnis: {
                  wahlvorschlagID: wahlvorschlag.identifikator,
                  kandidatID: kandidat.identifikator,
                  wahlvorschlagsOrdnungszahl: wahlvorschlag.ordnungszahl,
                  ergebnis: null,
                  numIndex: null,
                },
                kandidat,
              });
            }
          }
        }
      }

      console.log(`loading Result`, result);
      scorableWahlvorschlaege.value = result;
    } finally {
      isLoading.value = false;
    }
  }

  async function saveErgebnisse() {
    isSaving.value = true;

    try {
      const ergebnisValuesToSave = scorableWahlvorschlaege.value
        .flatMap((x) => x.scorableKandidaten)
        .map((x) => x.ergebnis)
        .filter((ergebnis) => ergebnis.ergebnis != null);

      const ergebnisse: Ergebnisse = {
        bezirkUndWahlIDStapelart: {
          wahlID,
          wahlbezirkID,
          stapelArt: StapelArt_BC,
        },
        ergebnisse: ergebnisValuesToSave,
      };
      await postErgebnisse(wahlbezirkID, wahlID, StapelArt_BC, ergebnisse);
    } finally {
      isSaving.value = false;
    }
  }

  return {
    isSaving,
    scorableWahlvorschlaege,
    loadWahlvorschlaegeAndErgebnisse,
    saveErgebnisse,
  };
}
