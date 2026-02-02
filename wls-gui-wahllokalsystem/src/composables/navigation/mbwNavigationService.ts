import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { ComputedRef } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { createMbwRoute } from "@/plugins/router/mbwRoutes.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const { electionWorkflowsStates } = storeToRefs(useWorkflowStore());

  const mbwWorkflow = computed(() =>
    electionWorkflowsStates.value.find(
      (status) =>
        status.bezirkUndWahlID.wahlID === wahlID &&
        status.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    )
  );

  const navigation: ComputedRef<NavigationDefinition[]> = computed(() => {
    if (!mbwWorkflow.value) return [];

    return [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRoute: createMbwRoute(
          MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRoute: createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRoute: createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_A_AND_B,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[StapelArtEnum.MbwDUngueltig]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: createMbwRoute(
          MbwRoutesEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRoute: createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_BC,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Niederschrift`,
        targetRoute: createMbwRoute(
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
