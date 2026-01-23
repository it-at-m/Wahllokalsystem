import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export const NullNextStepImpl: ElectionSpecificNextStep = {
  getNextRoute(): RouteLocationAsRelativeGeneric | null {
    return null;
  },
};

export const MBWNestStepImpl: ElectionSpecificNextStep = {
  getNextRoute(wahlstatus: Status): RouteLocationAsRelativeGeneric | null {
    if (!wahlstatus.schnellmeldung.gedruckt) {
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
