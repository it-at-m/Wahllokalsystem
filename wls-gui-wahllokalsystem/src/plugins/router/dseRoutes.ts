import type { RouteRecordRawWithoutName } from "@/types/navigation/RouteRecordRawWithoutName.ts";
import type { RouteRecordRaw } from "vue-router";

import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";
import BeschlussfassungView from "@/views/dse/BeschlussfassungView.vue";
import MonitoringView from "@/views/dse/MonitoringView.vue";
import StimmzettelerfassungView from "@/views/dse/StimmzettelerfassungView.vue";

const BASE_PATH_DSE = "/DSE/wahl/:wahlId/wahlbezirk/:wahlbezirkId";

const dseRoutesRecord: Record<DseStepsEnum, RouteRecordRawWithoutName> = {
  [DseStepsEnum.DSE_STIMMZETTELERFASSUNG]: {
    path: BASE_PATH_DSE + "/stimmzettelerfassung",
    component: StimmzettelerfassungView,
  },
  [DseStepsEnum.DSE_MONITORING]: {
    path: BASE_PATH_DSE + "/monitoring",
    component: MonitoringView,
  },
  [DseStepsEnum.DSE_BESCHLUSSFASSUNG]: {
    path: BASE_PATH_DSE + "/beschlussfassung",
    component: BeschlussfassungView,
  },
};

export const dseRouteDefinitions: RouteRecordRaw[] = Object.entries(
  dseRoutesRecord
).map(
  ([routeName, { beforeEnter, component, path }]) =>
    ({
      name: routeName,
      path,
      component,
      beforeEnter,
    }) as RouteRecordRaw
);
