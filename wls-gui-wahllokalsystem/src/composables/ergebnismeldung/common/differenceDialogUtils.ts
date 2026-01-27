import type { DifferenceDialogItem } from "@/types/ergebnismeldung/common/DifferenceDialogItem.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
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
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export function useDifferenceDialogUtils(wahlId = "") {
  const { wahlscheine } = storeToRefs(useWahlscheineStore());
  const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
  const { wahlenActions } = useWahlenStore();
  const { isUWB } = storeToRefs(useUserStore());
  const { getWahlbezirkIdFromWahlMetaDataByWahlId } = useUserStore();
  const { getBegruendungStimmzettelumschlaege, postBegruendung } =
    useErgebnisService();
  const { getStimmzettelTermForWahl, getWahlscheineOrStimmabgabevermerkeTerm } =
    useTextFormatter();

  const anzahlWahlscheineOrStimmabgabevermerke = computed(() =>
    isUWB.value
      ? // @ts-expect-error: noUncheckedIndexedAccess for wahldaten[0] | siehe #2008
        stimmabgabevermerke.value
          .find((vermerk) => vermerk.wahldaten[0]?.wahlID === wahlId)
          ?.wahldaten[0].eingenommeneWahlscheine.get(
            EingenommenerWahlscheinStimmzettelartEnum.Klein
          )
      : wahlscheine.value.find(
          (wahlschein) => wahlschein.bezirkUndWahlID.wahlID === wahlId
        )?.stimmabgabevermerke
  );
  const anzahlStimmzettel = computed(
    () =>
      wahlenActions.getWahlOrUndefinedById(wahlId)?.stimmzettelumschlaege
        .anzahlWaehler
  );
  const isWahlscheineUnequalToStimmzettel = computed(
    () =>
      anzahlWahlscheineOrStimmabgabevermerke.value != null &&
      anzahlStimmzettel.value != null &&
      anzahlWahlscheineOrStimmabgabevermerke.value !== anzahlStimmzettel.value
  );

  async function getBegruendung() {
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

  async function saveBegruendung(dialog: DifferenceDialogItem) {
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

  return {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
    getBegruendung,
    saveBegruendung,
    updateValidationStateForBegruendung,
    getDialogContent,
  };
}
