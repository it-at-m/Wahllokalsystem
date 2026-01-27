import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";

export function useMultipleDifferenceDialogUtils() {
  const { isUWB } = storeToRefs(useUserStore());
  const { saveStimmabgabevermerke } = useStimmabgabevermerkeStore();
  const { saveWahlscheine } = useWahlscheineStore();
  const { updateValidationStateForBegruendung, saveBegruendung } =
    useDifferenceDialogUtils();

  const dialogs = ref<DifferenceDialogItem[]>([]);

  async function onSaveClicked() {
    dialogs.value = [];
    if (isUWB.value) {
      await _checkForDifferenceInStimmabgabevermerke();
    } else {
      await _checkForDifferenceInWahlscheine();
    }
  }

  async function onConfirmClicked(dialog: DifferenceDialogItem) {
    dialog.isVisible = false;

    await saveBegruendung(dialog);

    if (dialogs.value.filter((dialog) => dialog.isVisible).length === 0) {
      if (isUWB.value) {
        await saveStimmabgabevermerke();
      } else {
        await saveWahlscheine();
      }
    }
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
      getBegruendung,
    } = useDifferenceDialogUtils(wahlId);

    if (isWahlscheineUnequalToStimmzettel.value) {
      const begruendungStimmzettel = await getBegruendung();

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

  return {
    dialogs,
    onSaveClicked,
    onConfirmClicked,
  };
}
