import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useStimmabgabevermerkeStore } from "@/stores/stimmabgabevermerkeStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWahlscheineStore } from "@/stores/wahlscheineStore.ts";
import { EingenommenerWahlscheinStimmzettelartEnum } from "@/types/stimmabgabevermerke/EingenommenerWahlscheinStimmzettelartEnum.ts";

export function useDifferenceDialogUtils(wahlId: string) {
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

  return {
    anzahlWahlscheineOrStimmabgabevermerke,
    anzahlStimmzettel,
    isWahlscheineUnequalToStimmzettel,
  };
}
