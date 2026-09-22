import type { RouteRecordRawWithoutName } from "@/types/navigation/RouteRecordRawWithoutName.ts";

import { type RouteRecordRaw } from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { allGuards, anyGuard } from "@/composables/navigation/routerUtils.ts";
import { MbwStepsEnum } from "@/types/navigation/MbwStepsEnum.ts";
import BeschlussfassungView from "@/views/dse/BeschlussfassungView.vue";
import MonitoringView from "@/views/dse/MonitoringView.vue";
import StimmzettelerfassungView from "@/views/dse/StimmzettelerfassungView.vue";
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
  requireRoleSchriftfuehrung,
  requireRoleErfassungteam,
} = useNavigationGuards();
const BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM =
  "/MBW/wahl/:wahlId/wahlbezirk/:wahlbezirkId";

const BASE_PATH_DSE = "/MBW/DSE/wahl/:wahlId/wahlbezirk/:wahlbezirkId";

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
    beforeEnter: [...auszaehlungPrerequisiteGuards, requireRoleSchriftfuehrung],
  },
  [MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG]: {
    path: BASE_PATH_DSE + "/stimmzettelerfassung",
    component: StimmzettelerfassungView,
    beforeEnter: anyGuard(
      // Conditions ROLE Schriftfuerer
      allGuards(
        isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
        requireRoleSchriftfuehrung
      ),
      // Conditions ROLE Erfassungsteam
      allGuards(requireRoleErfassungteam)
    ),
  },
  [MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS]: {
    path: BASE_PATH_DSE + "/monitoring",
    component: MonitoringView,
    beforeEnter: [
      requireRoleSchriftfuehrung,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG),
    ],
  },
  [MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG]: {
    path: BASE_PATH_DSE + "/beschlussfassung",
    component: BeschlussfassungView,
    beforeEnter: [
      requireRoleSchriftfuehrung,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG),
      isStepDoneInElectionState(
        MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS
      ),
    ],
  },
  [MbwStepsEnum.MBW_STAPEL_E]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM + "/stapelE",
    component: MBWStapelEView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      requireRoleSchriftfuehrung,
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
      requireRoleSchriftfuehrung,
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
      requireRoleSchriftfuehrung,
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
      requireRoleSchriftfuehrung,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      anyGuard(
        // Conditions DSE
        allGuards(
          isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG),
          isStepDoneInElectionState(
            MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS
          ),
          isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG)
        ),
        // Conditions Stapelerfassung
        allGuards(
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_A_AND_B)
        )
      ),
    ],
  },
  [MbwStepsEnum.MBW_STAPEL_BC]: {
    path:
      BASE_PATH_MBW_WAHLBEZIRK_WITH_WAHLID_AND_WAHLBEZIRKID_PARAM + "/stapelBC",
    component: MBWStapelBCView,
    beforeEnter: [
      ...auszaehlungPrerequisiteGuards,
      requireRoleSchriftfuehrung,
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
      requireRoleSchriftfuehrung,
      isStepDoneInElectionState(MbwStepsEnum.MBW_AUSZAEHLUNG_STIMMZETTEL),
      isStepDoneInElectionState(MbwStepsEnum.MBW_SCHNELLMELDUNG),
      anyGuard(
        // Conditions DSE
        allGuards(
          isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_STIMMZETTELERFASSUNG),
          isStepDoneInElectionState(
            MbwStepsEnum.MBW_DSE_MONITORING_ERFASSUNGSSTATUS
          ),
          isStepDoneInElectionState(MbwStepsEnum.MBW_DSE_BESCHLUSSFASSUNG)
        ),
        // Conditions Stapelerfassung
        allGuards(
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_E),
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_D_UNGUELTIG),
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_A_AND_B),
          isStepDoneInElectionState(MbwStepsEnum.MBW_STAPEL_BC)
        )
      ),
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
