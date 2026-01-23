import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { mbwRoutesRecord } from "@/plugins/router/mbwRoutes.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const { status } = storeToRefs(useStatusStore());

  const mbwStatus = computed(() =>
    status.value.find(
      (status) =>
        status.bezirkUndWahlID.wahlID === wahlID &&
        status.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    )
  );

  const navigation: ComputedRef<NavigationDefinition[]> = computed(() => {
    if (!mbwStatus.value) return [];

    return [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_D,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_A_AND_B,
          wahlID,
          wahlbezirkID
        ),
        disabled: status
          ? !mbwStatus.value.stepsDone[StapelArtEnum.MbwDUngueltig]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_STAPEL_BC,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Niederschrift`,
        targetRoute: mbwRoutesRecord.createRoute(
          MbwRoutesEnum.MBW_NIEDERSCHRIFT,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
    ];
  });

  return {
    navigation,
  };
}
