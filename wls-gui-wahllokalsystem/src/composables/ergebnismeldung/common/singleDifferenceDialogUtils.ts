import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { ref } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export function useSingleDifferenceDialogUtils(
  wahlId: string,
  wahlbezirkId: string
) {
  const {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
    updateValidationStateForBegruendung,
  } = useDifferenceDialogUtils(wahlId);
  const { stimmzettelumschlaegeActions } = useWahlenStore();
  const { wahlenActions } = useWahlenStore();
  const { getBegruendungStimmzettelumschlaege, postBegruendung } =
    useErgebnisService();
  const { getStimmzettelTermForWahl, getWahlscheineOrStimmabgabevermerkeTerm } =
    useTextFormatter();

  const dialog = ref<DifferenceDialogItem>();

  async function checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege() {
    if (!isWahlscheineUnequalToStimmzettel.value) {
      await _saveStimmzettelumschlaege();
    } else {
      const begruendungStimmzettel = await _getBegruendung();
      dialog.value = {
        isVisible: true,
        differenceBegruendung: {
          wahlId: wahlId,
          begruendung: begruendungStimmzettel?.grund || "",
          isBegruendungValid: false,
          anzahlWahlscheineOrStimmabgabevermerke:
            anzahlWahlscheineOrStimmabgabevermerke.value,
          anzahlStimmzettel: anzahlStimmzettel.value,
        },
      };
      updateValidationStateForBegruendung(dialog.value.differenceBegruendung);
    }
  }

  async function saveBegruendungAndStimmzettelumschlaege() {
    if (dialog.value) {
      dialog.value.isVisible = false;
      await _saveBegruendung();
    }
    await _saveStimmzettelumschlaege();
  }

  function getDialogContent() {
    return dialog.value
      ? `Die Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()} (${
          dialog.value.differenceBegruendung
            .anzahlWahlscheineOrStimmabgabevermerke
        }) unterscheidet sich um ${Math.abs(
          (dialog.value.differenceBegruendung
            .anzahlWahlscheineOrStimmabgabevermerke ?? 0) -
            (dialog.value.differenceBegruendung.anzahlStimmzettel ?? 0)
        )} von der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(dialog.value.differenceBegruendung.wahlId))} (${dialog.value.differenceBegruendung.anzahlStimmzettel})`
      : "";
  }

  async function _saveStimmzettelumschlaege() {
    await stimmzettelumschlaegeActions.saveStimmzettelumschlaege(wahlId);
  }

  async function _getBegruendung() {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlId);
    let begruendungStimmzettel;
    if (wahl) {
      begruendungStimmzettel = await getBegruendungStimmzettelumschlaege(
        wahl,
        wahlbezirkId,
        "",
        false
      );
    }
    return begruendungStimmzettel;
  }

  async function _saveBegruendung() {
    if (dialog.value) {
      await postBegruendung(
        {
          wahlID: dialog.value.differenceBegruendung.wahlId,
          stapelart: StapelArtEnum.StimmzettelUmschlaege,
          grund: dialog.value.differenceBegruendung.begruendung,
          unstimmigkeiten: true,
        },
        wahlbezirkId
      );
    }
  }

  return {
    dialog,
    isWahlscheineUnequalToStimmzettel,
    checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege,
    saveBegruendungAndStimmzettelumschlaege,
    updateValidationStateForBegruendung,
    getDialogContent,
  };
}
