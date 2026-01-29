import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
} from "@/constants.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";

export function useMultipleDifferenceDialogUtils() {
  const { isUWB } = storeToRefs(useUserStore());
  const { saveStimmabgabevermerke } = useStimmabgabevermerkeStore();
  const { saveWahlscheine } = useWahlscheineStore();
  const { wahlenActions } = useWahlenStore();
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getBegruendungStimmzettelumschlaege, postBegruendung } =
    useErgebnisService();
  const { getStimmzettelTermForWahl, getWahlscheineOrStimmabgabevermerkeTerm } =
    useTextFormatter();

  const dialogs = ref<DifferenceDialogItem[]>([]);

  async function checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine() {
    dialogs.value = [];
    if (isUWB.value) {
      await _checkForDifferenceInStimmabgabevermerke();
    } else {
      await _checkForDifferenceInWahlscheine();
    }
  }

  async function saveBegruendungAndStimmabgabevermerkeWahlscheine(
    dialog: DifferenceDialogItem
  ) {
    dialog.isVisible = false;

    await _saveBegruendung(dialog);

    if (dialogs.value.filter((dialog) => dialog.isVisible).length === 0) {
      if (isUWB.value) {
        await saveStimmabgabevermerke();
      } else {
        await saveWahlscheine();
      }
    }
  }

  function updateValidationStateForBegruendung(
    dialog: DifferenceDialogItem
  ): void {
    const value = dialog.begruendung;
    dialog.isBegruendungValid =
      value.length >= MIN_LENGTH_FOR_BEGRUENDUNG &&
      value.length <= MAX_LENGTH_FOR_TEXT_INPUT;
  }

  function getDialogContent(dialog: DifferenceDialogItem) {
    return `Die Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()} (${
      dialog.anzahlWahlscheineOrStimmabgabevermerke
    }) unterscheidet sich um
        ${Math.abs(
          (dialog.anzahlWahlscheineOrStimmabgabevermerke ?? 0) -
            (dialog.anzahlStimmzettel ?? 0)
        )}
        von der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(dialog.wahlId))} (${dialog.anzahlStimmzettel})`;
  }

  async function _checkForDifferenceInStimmabgabevermerke() {
    const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
    for (const vermerk of stimmabgabevermerke.value) {
      // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
      const wahlId = vermerk.wahldaten[0].wahlID;
      await _checkForDifferenceDialogForWahl(wahlId);
    }
    await _openDialogsOrSave(saveStimmabgabevermerke);
  }

  async function _checkForDifferenceInWahlscheine() {
    const { wahlscheine } = storeToRefs(useWahlscheineStore());
    for (const wahlschein of wahlscheine.value) {
      const wahlId = wahlschein.bezirkUndWahlID.wahlID;
      await _checkForDifferenceDialogForWahl(wahlId);
    }
    await _openDialogsOrSave(saveWahlscheine);
  }

  async function _checkForDifferenceDialogForWahl(wahlId: string) {
    const {
      anzahlWahlscheineOrStimmabgabevermerke,
      anzahlStimmzettel,
      isWahlscheineUnequalToStimmzettel,
    } = useDifferenceDialogUtils(wahlId);
    if (isWahlscheineUnequalToStimmzettel.value) {
      const begruendungStimmzettel = await _getBegruendung(wahlId);

      const dialogItem = {
        isVisible: false,
        wahlId: wahlId,
        begruendung: begruendungStimmzettel?.grund || "",
        isBegruendungValid: false,
        anzahlWahlscheineOrStimmabgabevermerke:
          anzahlWahlscheineOrStimmabgabevermerke.value,
        anzahlStimmzettel: anzahlStimmzettel.value,
      };

      updateValidationStateForBegruendung(dialogItem);

      _addNewItemToDialogsOrUpdateExistingItem(dialogItem, wahlId);
    }
  }

  async function _openDialogsOrSave(saveFunction: () => Promise<void>) {
    if (dialogs.value.length === 0) {
      await saveFunction();
    } else {
      dialogs.value.forEach((dialog) => {
        dialog.isVisible = true;
      });
    }
  }

  function _addNewItemToDialogsOrUpdateExistingItem(
    dialogItem: DifferenceDialogItem,
    wahlId: string
  ) {
    const existingDialogIndex = dialogs.value.findIndex(
      (dialog) => dialog.wahlId === wahlId
    );
    if (existingDialogIndex >= 0) {
      dialogs.value[existingDialogIndex] = dialogItem;
    } else {
      dialogs.value.push(dialogItem);
    }
  }

  async function _getBegruendung(wahlId: string) {
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

  async function _saveBegruendung(dialog: DifferenceDialogItem) {
    const wahlbezirkId = getWahlbezirkIdFromWahlMetaDataByWahlId(dialog.wahlId);
    if (wahlbezirkId) {
      await postBegruendung(
        {
          wahlID: dialog.wahlId,
          stapelart: StapelArtEnum.StimmzettelUmschlaege,
          grund: dialog.begruendung,
          unstimmigkeiten: true,
        },
        wahlbezirkId
      );
    }
  }

  return {
    dialogs,
    checkForDifferencesAndAddDialogsOrSaveStimmabgabevermerkeWahlscheine,
    saveBegruendungAndStimmabgabevermerkeWahlscheine,
    updateValidationStateForBegruendung,
    getDialogContent,
  };
}
