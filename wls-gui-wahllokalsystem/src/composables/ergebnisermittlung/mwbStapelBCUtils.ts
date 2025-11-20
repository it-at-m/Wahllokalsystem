import type { WahlvorschlagWithKandidatenErgebnissen } from "@/types/ergebnisermittlung/WahlvorschlagWithKandidatenErgebnissen.ts";
import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";
import type { Ref } from "vue";

import { ref } from "vue";

import { useLogging } from "@/composables/common/logging.ts";
import { useWahlvorschlagWithKandidatenErgebnissenMapper } from "@/composables/ergebnisermittlung/wahlvorschlagWithKandidatenErgebnissenMapper.ts";
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
  const { toErgebnisse, toWahlvorschlagWithKandidatenErgebnissen } =
    useWahlvorschlagWithKandidatenErgebnissenMapper();
  const { logError } = useLogging("useMwbStapelBCUtils");

  const isLoading = ref(false);
  const isSaving = ref(false);
  const wahlvorschlaegeWithKandidatenErgebnissen: Ref<
    WahlvorschlagWithKandidatenErgebnissen[]
  > = ref([]);

  async function loadWahlvorschlaegeAndErgebnisse() {
    isLoading.value = true;
    try {
      const wahlvorschlaege = await _loadAndSortWahlvorschlaege();
      const ergebnisse = await getErgebnisse(
        wahlbezirkID,
        wahlID,
        StapelArt_BC,
        false
      );

      wahlvorschlaegeWithKandidatenErgebnissen.value = [
        ...wahlvorschlaege.wahlvorschlaege,
      ].map((wahlvorschlag) =>
        toWahlvorschlagWithKandidatenErgebnissen(wahlvorschlag, ergebnisse)
      );
    } catch (error) {
      logError("loading of wahlvorschlaege and results failed", error);
    } finally {
      isLoading.value = false;
    }
  }

  async function saveErgebnisse() {
    isSaving.value = true;

    try {
      const ergebnisValuesToSave =
        wahlvorschlaegeWithKandidatenErgebnissen.value
          .flatMap((wahlvorschlag) => wahlvorschlag.kandidatenErgebnisse)
          .map((kandidatErgebnis) => kandidatErgebnis.ergebnis)
          .filter((ergebnis) => ergebnis.ergebnis != null);

      const ergebnisse = toErgebnisse(
        ergebnisValuesToSave,
        wahlbezirkID,
        wahlID,
        StapelArt_BC
      );
      await postErgebnisse(wahlbezirkID, wahlID, StapelArt_BC, ergebnisse);
    } catch (error) {
      logError("saving ergebnisse failed", error);
    } finally {
      isSaving.value = false;
    }
  }

  function _loadAndSortWahlvorschlaege() {
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
      wahlvorschlag.kandidaten = [...wahlvorschlag.kandidaten].sort(
        compareKandidatenByListenPosition
      );
    }
  }

  return {
    isLoading,
    isSaving,
    wahlvorschlaegeWithKandidatenErgebnissen,
    loadWahlvorschlaegeAndErgebnisse,
    saveErgebnisse,
  };
}
