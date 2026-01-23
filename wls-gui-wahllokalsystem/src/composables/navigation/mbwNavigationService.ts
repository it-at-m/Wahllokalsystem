import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const { getStatus } = useStatusStore();
  const status = getStatus(wahlID, wahlbezirkID);

  const navigation: ComputedRef<NavigationDefinition<MbwRoutesEnum>[]> =
    computed(() => [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRouteName: MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
        disabled: false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRouteName: MbwRoutesEnum.MBW_STAPEL_D,
        disabled: false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRouteName: MbwRoutesEnum.MBW_STAPEL_A_AND_B,
        disabled: status
          ? !status.stepsDone[StapelArtEnum.MbwDUngueltig]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRouteName: MbwRoutesEnum.MBW_SCHNELLMELDUNG,
        disabled: false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRouteName: MbwRoutesEnum.MBW_STAPEL_BC,
        disabled: false,
      },
      {
        title: `Niederschrift`,
        targetRouteName: MbwRoutesEnum.MBW_NIEDERSCHRIFT,
        disabled: false,
      },
    ]);

  return {
    navigation,
  };
}
