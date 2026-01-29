import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { ref } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export function useSingleDifferenceDialogUtils(wahlId: string) {
  const {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
  } = useDifferenceDialogUtils(wahlId);
  const { stimmzettelumschlaegeActions } = useWahlenStore();
  const { wahlenActions } = useWahlenStore();
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
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
        wahlId: wahlId,
        begruendung: begruendungStimmzettel?.grund || "",
        isBegruendungValid: false,
        anzahlWahlscheineOrStimmabgabevermerke:
          anzahlWahlscheineOrStimmabgabevermerke.value,
        anzahlStimmzettel: anzahlStimmzettel.value,
      };
      updateValidationStateForBegruendung();
    }
  }

  async function saveBegruendungAndStimmzettelumschlaege() {
    if (dialog.value) {
      dialog.value.isVisible = false;
      await _saveBegruendung();
    }
    await _saveStimmzettelumschlaege();
  }

  function updateValidationStateForBegruendung(): void {
    if (dialog.value) {
      const value = dialog.value.begruendung;
      dialog.value.isBegruendungValid =
        value.length >= MIN_LENGTH_FOR_BEGRUENDUNG &&
        value.length <= MAX_LENGTH_FOR_TEXT_INPUT;
    }
  }

  function getDialogContent() {
    return dialog.value
      ? `Die Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()} (${
          dialog.value.anzahlWahlscheineOrStimmabgabevermerke
        }) unterscheidet sich um
        ${Math.abs(
          (dialog.value.anzahlWahlscheineOrStimmabgabevermerke ?? 0) -
            (dialog.value.anzahlStimmzettel ?? 0)
        )}
        von der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(dialog.value.wahlId))} (${dialog.value.anzahlStimmzettel})`
      : "";
  }

  async function _saveStimmzettelumschlaege() {
    await stimmzettelumschlaegeActions.saveStimmzettelumschlaege(wahlId);
  }

  async function _getBegruendung() {
    const wahl = wahlenActions.getWahlOrUndefinedById(wahlId);
    const wahlbezirkId = getWahlbezirkIdFromWahlMetaDataByWahlId(wahlId);
    let begruendungStimmzettel;
    if (wahl && wahlbezirkId) {
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
      const wahlbezirkId = getWahlbezirkIdFromWahlMetaDataByWahlId(
        dialog.value.wahlId
      );
      if (wahlbezirkId) {
        await postBegruendung(
          {
            wahlID: dialog.value.wahlId,
            stapelart: StapelArtEnum.StimmzettelUmschlaege,
            grund: dialog.value.begruendung,
            unstimmigkeiten: true,
          },
          wahlbezirkId
        );
      }
    }
  }

  return {
    dialog,
    checkForDifferencesAndOpenDialogOrSaveStimmzettelumschlaege,
    saveBegruendungAndStimmzettelumschlaege,
    updateValidationStateForBegruendung,
    getDialogContent,
  };
}
