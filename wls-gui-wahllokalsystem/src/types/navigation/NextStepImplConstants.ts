import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { ElectionWorkflow } from "@/types/navigation/ElectionWorkflow.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export const NullNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(): RouteLocationAsRelativeGeneric | null {
    return null;
  },
};

export const MBWNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(
    wahlstatus: ElectionWorkflow
  ): RouteLocationAsRelativeGeneric | null {
    if (!wahlstatus.isSchnellmeldungDone) {
      return {
        name: MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
        params: {
          wahlId: wahlstatus.bezirkUndWahlID.wahlID,
          wahlbezirkId: wahlstatus.bezirkUndWahlID.wahlbezirkID,
        },
      };
    }
    return null;
  },
};
