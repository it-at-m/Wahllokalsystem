import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";

import { useMbwNavigationService } from "@/composables/navigation/mbwNavigationService.ts";

export const NullNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(): RouteLocationAsRelativeGenericWithStringName | null {
    return null;
  },
};

export const MBWNextStepImpl: ElectionSpecificNextStep = {
  getNextRouteOrNull(
    wahlID: string,
    wahlbezirkID: string
  ): RouteLocationAsRelativeGenericWithStringName | null {
    return useMbwNavigationService(wahlID, wahlbezirkID).getNextRouteOrNull();
  },
};
