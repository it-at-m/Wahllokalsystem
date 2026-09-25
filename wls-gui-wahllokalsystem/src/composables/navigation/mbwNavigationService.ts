import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";
import type { ComputedRef } from "vue";

import { storeToRefs } from "pinia";
import { computed } from "vue";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useInfomanagementStore } from "@/stores/infomanagementStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";

export function useMbwNavigationService(wahlID: string, wahlbezirkID: string) {
  const { getStimmzettelTermForWahlID } = useTextFormatter();

  const { electionWorkflowsStates } = storeToRefs(useWorkflowStore());
  const { hasRoleSchriftfuehrung } = storeToRefs(useUserStore());
  const { isDseAktiv } = storeToRefs(useInfomanagementStore());

  const mbwWorkflow = computed(() =>
    electionWorkflowsStates.value.find(
      (status) =>
        status.bezirkUndWahlID.wahlID === wahlID &&
        status.bezirkUndWahlID.wahlbezirkID === wahlbezirkID
    )
  );

  const navigation: ComputedRef<NavigationDefinition[]> = computed(() => {
    if (!mbwWorkflow.value) return [];

    const result: NavigationDefinition[] = [];

    if (isDseAktiv.value) {
      if (hasRoleSchriftfuehrung.value) {
        result.push(..._createNavigationForDseSchriftfuehrung());
      } else {
        result.push(..._createNavigationForDseErfassungsteam());
      }
    }
    //Stapelerfassung only for ROLE Schriftfuehrung
    else if (hasRoleSchriftfuehrung.value) {
      result.push(..._createNavigationForSchriftfuehrung());
    }
    return result;
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
    routeName: MbwStepsEnum,
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

  function _createNavigationForSchriftfuehrung() {
    return [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Bedenkliche Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_STAPEL_E,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[
              MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
            ]
          : false,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_STAPEL_E]
          : false,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_STAPEL_A_AND_B,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_STAPEL_A_AND_B]
          : false,
      },
      {
        title: `Kandidatinnen- und Kandidatenstimmen`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_STAPEL_BC,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_SCHNELLMELDUNG]
          : false,
      },
      {
        title: `Niederschrift`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_NIEDERSCHRIFT,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_STAPEL_BC]
          : false,
      },
    ];
  }

  function _createNavigationForDseSchriftfuehrung() {
    return [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Stimmzettelerfassung`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[
              MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL
            ]
          : false,
      },
      {
        title: `Monitoring`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[
              MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG
            ]
          : false,
      },
      {
        title: `Beschlussfassung`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[
              MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS
            ]
          : false,
      },
      {
        title: `Schnellmeldung`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_SCHNELLMELDUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG]
          : false,
      },
      {
        title: `Niederschrift`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_NIEDERSCHRIFT,
          wahlID,
          wahlbezirkID
        ),
        disabled: mbwWorkflow.value
          ? !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_SCHNELLMELDUNG] ||
            !mbwWorkflow.value.stepsDone[MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG]
          : false,
      },
    ];
  }

  function _createNavigationForDseErfassungsteam() {
    return [
      {
        title: `Stimmzettelerfassung`,
        targetRoute: _createMbwRoute(
          MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
    ];
  }

  return {
    navigation,
    getNextRouteOrNull,
  };
}
