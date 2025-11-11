import type { WahlvorschlagWithScorableKandidaten } from "@/types/ergebnisermittlung/WahlvorschlagWithScorableKandidaten.ts";
import type { Ergebnisse } from "@/types/ergebnismeldung/Ergebnisse.ts";
import type { Ref } from "vue";

import { ref } from "vue";

import { useWahlvorschlagWithScorableKandidatenMapper } from "@/composables/ergebnisermittlung/wahlvorschlagWithScorableKandidatenMapper.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/ergebnisService.ts";
import { useWahlvorschlaegeService } from "@/composables/wahlvorschlaege/wahlvorschlaegeService.ts";
import { useWahlvorschlagUtils } from "@/composables/wahlvorschlaege/wahlvorschlagUtils.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/StapelArtEnum.ts";

export function useMwbStapelBCUtils(wahlbezirkID: string, wahlID: string) {
  const StapelArt_BC = StapelArtEnum.MbwBC;

  const { getErgebnisse, postErgebnisse } = useErgebnisService();
  const { getWahlvorschlaege } = useWahlvorschlaegeService();
  const {
    compareKandidatenByListenPosition,
    sortWahlvorschlaegeByOrdnungszahl,
  } = useWahlvorschlagUtils();
  const { toWahlvorschlagWithScorableKandidaten } =
    useWahlvorschlagWithScorableKandidatenMapper();

  const isLoading = ref(false);
  const isSaving = ref(false);
  const scorableWahlvorschlaege: Ref<WahlvorschlagWithScorableKandidaten[]> =
    ref([]);

  async function loadWahlvorschlaegeAndErgebnisse() {
    isLoading.value = true;
    try {
      const wahlvorschlaege = await _loadAndSortWahlschlaege();
      const ergebnisse = await getErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArt_BC,
        false
      );

      scorableWahlvorschlaege.value = [...wahlvorschlaege.wahlvorschlaege].map(
        (wahlvorschlag) =>
          toWahlvorschlagWithScorableKandidaten(wahlvorschlag, ergebnisse)
      );
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

  function _loadAndSortWahlschlaege() {
    return getWahlvorschlaege(wahlID, wahlbezirkID)
      .then((wahlvorschlaege) =>
        sortWahlvorschlaegeByOrdnungszahl(wahlvorschlaege)
      )
      .then((wahlvorschlaege) => {
        wahlvorschlaege.wahlvorschlaege.forEach((wahlvorschlag) => {
          _sortKandidatenByListenPosition(wahlvorschlag);
        });
        return wahlvorschlaege;
      });
  }

  function _sortKandidatenByListenPosition(wahlvorschlag: Wahlvorschlag) {
    if (wahlvorschlag.kandidaten) {
      wahlvorschlag.kandidaten = new Set(
        [...wahlvorschlag.kandidaten].sort(compareKandidatenByListenPosition)
      );
    }
  }

  return {
    isLoading,
    isSaving,
    scorableWahlvorschlaege,
    loadWahlvorschlaegeAndErgebnisse,
    saveErgebnisse,
  };
}
