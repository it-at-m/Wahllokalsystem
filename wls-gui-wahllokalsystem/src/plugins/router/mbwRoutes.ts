import {
  type RouteLocationAsRelativeGeneric,
  type RouteRecordRaw,
} from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import ErfassungStimmzettelView from "@/views/ergebnismeldung/common/ErfassungStimmzettelView.vue";
import MBWNiederschriftView from "@/views/ergebnismeldung/MBW/MBWNiederschriftView.vue";
import MBWSchnellmeldungView from "@/views/ergebnismeldung/MBW/MBWSchnellmeldungView.vue";
import MBWStapelAandBView from "@/views/ergebnismeldung/MBW/MBWStapelAandBView.vue";
import MBWStapelBCView from "@/views/ergebnismeldung/MBW/MBWStapelBCView.vue";
import MBWStapelDView from "@/views/ergebnismeldung/MBW/MBWStapelDView.vue";

const { isStepDoneInElectionState } = useNavigationGuards();
const BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM =
  "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId";

type RouteRecordRawWithoutName = Omit<RouteRecordRaw, "name">;

const mbwRoutesRecord: Record<MbwRoutesEnum, RouteRecordRawWithoutName> = {
  [MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/auszaehlungStimmzettel",
    component: ErfassungStimmzettelView,
  },
  [MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/stapelDUngueltig",
    component: MBWStapelDView,
    beforeEnter: [
      isStepDoneInElectionState(MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
    ],
  },
  [MbwRoutesEnum.MBW_STAPEL_A_AND_B]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/stapelAandB",
    component: MBWStapelAandBView,
    beforeEnter: [
      isStepDoneInElectionState(MbwRoutesEnum.MBW_STAPEL_D_UNGUELTIG),
    ],
  },
  [MbwRoutesEnum.MBW_SCHNELLMELDUNG]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/schnellmeldung",
    component: MBWSchnellmeldungView,
    beforeEnter: [isStepDoneInElectionState(MbwRoutesEnum.MBW_STAPEL_A_AND_B)],
  },
  [MbwRoutesEnum.MBW_STAPEL_BC]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM + "/stapelBC",
    component: MBWStapelBCView,
    beforeEnter: [isStepDoneInElectionState(MbwRoutesEnum.MBW_SCHNELLMELDUNG)],
  },
  [MbwRoutesEnum.MBW_NIEDERSCHRIFT]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/niederschrift",
    component: MBWNiederschriftView,
    beforeEnter: [isStepDoneInElectionState(MbwRoutesEnum.MBW_STAPEL_BC)],
  },
};

export function createMbwRoute(
  routeName: MbwRoutesEnum,
  wahlId: string,
  wahlbezirkId: string
): RouteLocationAsRelativeGeneric {
  return {
    name: routeName,
    params: {
      wahlId,
      wahlbezirkId,
    },
  };
}

export const mbwRouteDefinitions: RouteRecordRaw[] = Object.entries(
  mbwRoutesRecord
).map(
  ([routeName, { beforeEnter, component, path }]) =>
    ({
      name: routeName,
      path,
      component,
      beforeEnter,
    }) as RouteRecordRaw
);
