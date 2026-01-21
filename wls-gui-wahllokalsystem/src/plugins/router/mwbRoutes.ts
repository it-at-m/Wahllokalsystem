import type { ComputedRef } from "vue";

import { computed } from "vue";
import { type RouteRecordRaw } from "vue-router";

import { useTextFormatter } from "@/composables/common/textFormatter.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import ErfassungStimmzettelView from "@/views/ergebnismeldung/common/ErfassungStimmzettelView.vue";
import MBWStapelAandBView from "@/views/ergebnismeldung/MBW/MBWStapelAandBView.vue";
import MBWStapelDView from "@/views/ergebnismeldung/MBW/MBWStapelDView.vue";

export const MbwRoutesEnum = {
  MBW_AUSZAEHLUNG_STIMMZETTEL: "MBW_AUSZAEHLUNG_STIMMZETTEL",
  MBW_STAPEL_D: "MBW_STAPEL_D",
  MBW_STAPEL_A_AND_B: "MBW_STAPEL_A_AND_B",
};
export type MbwRoutesEnum = (typeof MbwRoutesEnum)[keyof typeof MbwRoutesEnum];

export const RoutesEnum = {
  ...MbwRoutesEnum,
};
export type RoutesEnum = (typeof RoutesEnum)[keyof typeof RoutesEnum];

export const routeDefinitionsRecord: Record<RoutesEnum, RouteRecordRaw> = {
  MBW_AUSZAEHLUNG_STIMMZETTEL: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/auszaehlungStimmzettel",
    component: ErfassungStimmzettelView,
  },
  MBW_STAPEL_D: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelD",
    component: MBWStapelDView,
  },
  MBW_STAPEL_A_AND_B: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelAandB",
    component: MBWStapelAandBView,
    beforeEnter: [
      (to) => {
        const wahlID = to.params.wahlId;
        if (wahlID === undefined || Array.isArray(wahlID)) {
          return false;
        }

        const wahlState = useStatusStore().getWahl(wahlID).value;
        return wahlState[StapelArtEnum.MbwDUngueltig] === true; //not to simplify, or it would return undefined whicht is equal to true in router
      },
    ],
  },
};
export const routeDefinitions = Object.entries(routeDefinitionsRecord).map(
  ([routeName, routeDefinition]) => ({
    name: routeName,
    ...routeDefinition,
  })
);

export interface NavigationDefinition {
  title: string;
  targetRouteName: MbwRoutesEnum;
  disabled: boolean;
  nonDisabledState: WorkflowStepStateEnum;
}

export const WorflowStepStateEnum = {
  DONE: "DONE",
  IN_PROGRESS: "IN_PROGRESS",
};
export type WorkflowStepStateEnum =
  (typeof WorflowStepStateEnum)[keyof typeof WorflowStepStateEnum];

const { getStimmzettelTermForWahlID } = useTextFormatter();

export function useMbwRoutes(wahlID: string) {
  const theWahlState = useStatusStore().getWahl(wahlID);

  const navigation: ComputedRef<(NavigationDefinition & { title: string })[]> =
    computed(() => [
      {
        title: `Zählen der ${getStimmzettelTermForWahlID(wahlID)}`,
        targetRouteName: MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
        disabled: false,
        nonDisabledState: WorflowStepStateEnum.IN_PROGRESS,
      },
      {
        title: `Ungültige Stimmzettel`,
        targetRouteName: MbwRoutesEnum.MBW_STAPEL_D,
        disabled: false,
        nonDisabledState: WorflowStepStateEnum.IN_PROGRESS,
      },
      {
        title: `Gültige Stimmzettel`,
        targetRouteName: MbwRoutesEnum.MBW_STAPEL_A_AND_B,
        disabled: !theWahlState.value[StapelArtEnum.MbwDUngueltig],
        nonDisabledState: WorflowStepStateEnum.IN_PROGRESS,
      },
    ]);

  return {
    navigation,
  };
}
