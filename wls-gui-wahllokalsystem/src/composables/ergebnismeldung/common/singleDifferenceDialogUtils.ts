import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { ref } from "vue";

import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

export function useSingleDifferenceDialogUtils(wahlId: string) {
  const {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
    getBegruendung,
    saveBegruendung,
    updateValidationStateForBegruendung,
  } = useDifferenceDialogUtils(wahlId);
  const { stimmzettelumschlaegeActions } = useWahlenStore();

  const dialog = ref<DifferenceDialogItem>();

  async function onSaveClicked() {
    if (!isWahlscheineUnequalToStimmzettel.value) {
      await _saveStimmzettelumschlaege();
    } else {
      const begruendungStimmzettel = await getBegruendung();
      dialog.value = {
        isVisible: true,
        wahlId: wahlId,
        begruendung: begruendungStimmzettel?.grund || "",
        isBegruendungValid: false,
        anzahlWahlscheineOrStimmabgabevermerke:
          anzahlWahlscheineOrStimmabgabevermerke.value,
        anzahlStimmzettel: anzahlStimmzettel.value,
      };
      updateValidationStateForBegruendung(dialog.value);
    }
  }

  async function onConfirmClicked() {
    if (dialog.value) {
      dialog.value.isVisible = false;
      await saveBegruendung(dialog.value);
    }
    await _saveStimmzettelumschlaege();
  }

  async function _saveStimmzettelumschlaege() {
    await stimmzettelumschlaegeActions.saveStimmzettelumschlaege(wahlId);
  }

  return {
    dialog,
    onSaveClicked,
    onConfirmClicked,
  };
}
