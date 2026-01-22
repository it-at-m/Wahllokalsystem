import { type RouteRecordRaw } from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { StapelArtEnum } from "@/types/ergebnismeldung/common/StapelArtEnum.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";
import ErfassungStimmzettelView from "@/views/ergebnismeldung/common/ErfassungStimmzettelView.vue";
import MBWNiederschriftView from "@/views/ergebnismeldung/MBW/MBWNiederschriftView.vue";
import MBWSchnellmeldungView from "@/views/ergebnismeldung/MBW/MBWSchnellmeldungView.vue";
import MBWStapelAandBView from "@/views/ergebnismeldung/MBW/MBWStapelAandBView.vue";
import MBWStapelBCView from "@/views/ergebnismeldung/MBW/MBWStapelBCView.vue";
import MBWStapelDView from "@/views/ergebnismeldung/MBW/MBWStapelDView.vue";

const { isStepDoneInElectionState } = useNavigationGuards();

const mbwRoutesRecord: Record<MbwRoutesEnum, RouteRecordRaw> = {
  [MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/auszaehlungStimmzettel",
    component: ErfassungStimmzettelView,
  },
  [MbwRoutesEnum.MBW_STAPEL_D]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelD",
    component: MBWStapelDView,
  },
  [MbwRoutesEnum.MBW_STAPEL_A_AND_B]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelAandB",
    component: MBWStapelAandBView,
    beforeEnter: [isStepDoneInElectionState(StapelArtEnum.MbwDUngueltig)],
  },
  [MbwRoutesEnum.MBW_SCHNELLMELDUNG]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/schnellmeldung",
    component: MBWSchnellmeldungView,
  },
  [MbwRoutesEnum.MBW_STAPEL_BC]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/stapelBC",
    component: MBWStapelBCView,
  },
  [MbwRoutesEnum.MBW_NIEDERSCHRIFT]: {
    path: "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId/niederschrift",
    component: MBWNiederschriftView,
  },
};

export const mbwRouteDefinitions: RouteRecordRaw[] = Object.entries(
  mbwRoutesRecord
).map(([routeName, routeDefinition]) => ({
  name: routeName,
  ...routeDefinition,
}));
