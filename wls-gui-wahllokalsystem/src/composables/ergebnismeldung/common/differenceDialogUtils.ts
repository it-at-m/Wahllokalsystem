import type { DifferenceBegruendung } from "@/types/ergebnismeldung/common/DifferenceBegruendung.ts";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import {
  MAX_LENGTH_FOR_TEXT_INPUT,
  MIN_LENGTH_FOR_BEGRUENDUNG,
} from "@/constants.ts";
import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export function useDifferenceDialogUtils(wahlId = "") {
  const { wahlscheine } = storeToRefs(useWahlscheineStore());
  const { stimmabgabevermerke } = storeToRefs(useStimmabgabevermerkeStore());
  const { wahlenActions } = useWahlenStore();
  const { isUWB } = storeToRefs(useUserStore());

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

  function updateValidationStateForBegruendung(
    differenceBegruendung: DifferenceBegruendung
  ): void {
    const begruendung = differenceBegruendung.begruendung;
    differenceBegruendung.isBegruendungValid =
      begruendung.length >= MIN_LENGTH_FOR_BEGRUENDUNG &&
      begruendung.length <= MAX_LENGTH_FOR_TEXT_INPUT;
  }

  return {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
    updateValidationStateForBegruendung,
  };
}
