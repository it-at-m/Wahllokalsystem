import type { RouteRecordRawWithoutName } from "@/types/navigation/RouteRecordRawWithoutName.ts";

import { type RouteRecordRaw } from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import ErfassungStimmzettelView from "@/views/ergebnismeldung/common/ErfassungStimmzettelView.vue";
import MBWNiederschriftView from "@/views/ergebnismeldung/MBW/MBWNiederschriftView.vue";
import MBWSchnellmeldungView from "@/views/ergebnismeldung/MBW/MBWSchnellmeldungView.vue";
import MBWStapelAandBView from "@/views/ergebnismeldung/MBW/MBWStapelAandBView.vue";
import MBWStapelBCView from "@/views/ergebnismeldung/MBW/MBWStapelBCView.vue";
import MBWStapelDView from "@/views/ergebnismeldung/MBW/MBWStapelDView.vue";
import MBWStapelEView from "@/views/ergebnismeldung/MBW/MBWStapelEView.vue";

const {
  isStepDoneInElectionState,
  permitNavigationWhenWahlvorstandIsErfasstOrAllElectionsAreFinished,
  requiresWahlhandlungErfasstWhenWahlbezirksArtUwb,
  requiresWahlbriefzulassungErfasstWhenWahlbezirksArtBwb,
  requiresStimmabgabevermerkeErfasstWhenWahlbezirksArtUwb,
  requiresAnzahlWahlscheineErfasstWhenWahlbezirksArtBwb,
} = useNavigationGuards();
const BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM =
  "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId";

const auszaehlungPrerequisiteGuards = [
  permitNavigationWhenWahlvorstandIsErfasstOrAllElectionsAreFinished,
  requiresWahlhandlungErfasstWhenWahlbezirksArtUwb,
  requiresWahlbriefzulassungErfasstWhenWahlbezirksArtBwb,
  requiresStimmabgabevermerkeErfasstWhenWahlbezirksArtUwb,
  requiresAnzahlWahlscheineErfasstWhenWahlbezirksArtBwb,
];

const mbwRoutesRecord: Record<MbwStepsEnum, RouteRecordRawWithoutName> = {
  [MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/auszaehlungStimmzettel",
    component: ErfassungStimmzettelView,
    beforeEnter: [...auszaehlungPrerequisiteGuards],
  },
  [MbwStepsEnum.MBW_STAPEL_E]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM + "/stapelE",
    component: MBWStapelEView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
    ],
  },
  [MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/stapelDUngueltig",
    component: MBWStapelDView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
    ],
  },
  [MbwStepsEnum.MBW_STAPEL_A_AND_B]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/stapelAandB",
    component: MBWStapelAandBView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
    ],
  },
  [MbwStepsEnum.MBW_SCHNELLMELDUNG]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/schnellmeldung",
    component: MBWSchnellmeldungView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_A_AND_B),
    ],
  },
  [MbwStepsEnum.MBW_STAPEL_BC]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM + "/stapelBC",
    component: MBWStapelBCView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_A_AND_B),
      isStepDoneInElectionState(MbwStepsEnum.MBW_SCHNELLMELDUNG),
    ],
  },
  [MbwStepsEnum.MBW_NIEDERSCHRIFT]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM +
      "/niederschrift",
    component: MBWNiederschriftView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_A_AND_B),
      isStepDoneInElectionState(MbwStepsEnum.MBW_SCHNELLMELDUNG),
      isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_BC),
    ],
  },
};

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
