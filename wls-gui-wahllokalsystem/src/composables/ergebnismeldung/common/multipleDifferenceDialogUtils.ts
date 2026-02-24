import type { DifferenceBegruendung } from "@/types/ergebnismeldung/common/DifferenceBegruendung.ts";
import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { storeToRefs } from "pinia";
import { ref } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useDifferenceDialogUtils } from "@/composables/ergebnismeldung/common/differenceDialogUtils.ts";
import { useErgebnisService } from "@/composables/ergebnismeldung/common/ergebnisService.ts";
import { useNavigationUtils } from "@/composables/navigation/navigationUtils.ts";
import router from "@/plugins/router.ts";
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
  const { updateValidationStateForBegruendung } = useDifferenceDialogUtils();
  const { getNextRoute } = useNavigationUtils();

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

    await _saveBegruendung(dialog.differenceBegruendung);

    if (dialogs.value.filter((dialog) => dialog.isVisible).length === 0) {
      if (isUWB.value) {
        await saveStimmabgabevermerke();
        await router.push(getNextRoute());
      } else {
        await saveWahlscheine();
        await router.push(getNextRoute());
      }
    }
  }

  function getDialogContent(differenceBegruendung: DifferenceBegruendung) {
    return `Die Anzahl der ${getWahlscheineOrStimmabgabevermerkeTerm()} (${
      differenceBegruendung.anzahlWahlscheineOrStimmabgabevermerke
    }) unterscheidet sich um ${Math.abs(
      (differenceBegruendung.anzahlWahlscheineOrStimmabgabevermerke ?? 0) -
        (differenceBegruendung.anzahlStimmzettel ?? 0)
    )} von der Anzahl der ${getStimmzettelTermForWahl(wahlenActions.getWahlOrUndefinedById(differenceBegruendung.wahlId))} (${differenceBegruendung.anzahlStimmzettel})`;
  }

  async function _checkForDifferenceInStimmabgabevermerke() {
    const { stimmabgabevermerke } = useStimmabgabevermerkeStore();
    for (const vermerk of stimmabgabevermerke) {
      // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
      const wahlId = vermerk.wahldaten[0].wahlID;
      await _checkForDifferenceDialogForWahl(wahlId);
    }
    await _openDialogsOrSave(saveStimmabgabevermerke);
  }

  async function _checkForDifferenceInWahlscheine() {
    const { wahlscheine } = useWahlscheineStore();
    for (const wahlschein of wahlscheine) {
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
        differenceBegruendung: {
          wahlId: wahlId,
          begruendung: begruendungStimmzettel?.grund || "",
          isBegruendungValid: false,
          anzahlWahlscheineOrStimmabgabevermerke:
            anzahlWahlscheineOrStimmabgabevermerke.value,
          anzahlStimmzettel: anzahlStimmzettel.value,
        },
      };

      updateValidationStateForBegruendung(dialogItem.differenceBegruendung);

      _addNewItemToDialogsOrUpdateExistingItem(dialogItem, wahlId);
    }
  }

  async function _openDialogsOrSave(saveFunction: () => Promise<void>) {
    if (dialogs.value.length === 0) {
      await saveFunction();
      await router.push(getNextRoute());
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
      (dialog) => dialog.differenceBegruendung.wahlId === wahlId
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

  async function _saveBegruendung(
    differenceBegruendung: DifferenceBegruendung
  ) {
    const wahlbezirkId = getWahlbezirkIdFromWahlMetaDataByWahlId(
      differenceBegruendung.wahlId
    );
    if (wahlbezirkId) {
      await postBegruendung(
        {
          wahlID: differenceBegruendung.wahlId,
          stapelart: StapelArtEnum.StimmzettelUmschlaege,
          grund: differenceBegruendung.begruendung,
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
