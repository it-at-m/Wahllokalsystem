import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const status = useStatusStore().getStatus(wahlID, wahlbezirkID);
  if (!status) {
    return {
      navigation: computed(() => []) as ComputedRef<NavigationDefinition[]>,
    };
  }

  //TODO das MbwRoutesEnum wird aktuell nur einmal verwendet; ist es dann so sinnvoll?
  const navigation: ComputedRef<NavigationDefinition[]> = computed(() => [
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
      disabled: !status.stepsDone[StapelArtEnum.MbwDUngueltig],
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
