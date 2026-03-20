import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export const NullNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(): RouteLocationAsRelativeGeneric | null {
    return null;
  },
};

export const MBWNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(
    wahlstatus: ElectionWorkflowState
  ): RouteLocationAsRelativeGeneric | null {
    for (const route of Object.values(MbwRoutesEnum)) {
      if (!wahlstatus.stepsDone[route]) {
        return {
          name: route,
          params: {
            wahlId: wahlstatus.bezirkUndWahlID.wahlID,
            wahlbezirkId: wahlstatus.bezirkUndWahlID.wahlbezirkID,
          },
        };
      }
    }
    return null;
  },
};
