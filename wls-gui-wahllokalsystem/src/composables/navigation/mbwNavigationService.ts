import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";
import type { ComputedRef } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
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
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Bedenkliche Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_E,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[
              MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
            ]
          : false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwRoutesEnum.MBW_STAPEL_E]
          : false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_A_AND_B,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwRoutesEnum.MBW_STAPEL_A_AND_B]
          : false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_STAPEL_BC,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwRoutesEnum.MBW_SCHNELLMELDUNG]
          : false,
      },
      {
        title: `Niederschrift`,
        targetRoute: _createMbwRoute(
          MbwRoutesEnum.MBW_NIEDERSCHRIFT,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwRoutesEnum.MBW_STAPEL_BC]
          : false,
      },
    ];
  });

  function getNextRouteOrNull() {
    const navigationWithStepNotDone = navigation.value.find(
      (navigationValue) =>
        !mbwWorkflow.value?.stepsDone[navigationValue.targetRoute.name]
    );
    return navigationWithStepNotDone
      ? navigationWithStepNotDone.targetRoute
      : null;
  }

  function _createMbwRoute(
    routeName: MbwRoutesEnum,
    wahlId: string,
    wahlbezirkId: string
  ): RouteLocationAsRelativeGenericWithStringName {
    return {
      name: routeName,
      params: {
        wahlId,
        wahlbezirkId,
      },
    };
  }

  return {
    navigation,
    getNextRouteOrNull,
  };
}
