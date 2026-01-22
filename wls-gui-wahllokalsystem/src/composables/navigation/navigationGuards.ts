import type {
  NavigationGuard,
  RouteLocationNormalizedGeneric,
} from "vue-router";

import { useStatusStore } from "@/stores/statusStore.ts";

type NavigationGuardFactory<T> = (options: T) => NavigationGuard;

export function useNavigationGuards() {
  const isStepDoneInElectionState: NavigationGuardFactory<string> =
    (requiredStep) => (to) =>
      _isStepDone(to, requiredStep);

  function _isStepDone(
    to: RouteLocationNormalizedGeneric,
    requiredStep: string
  ) {
    const { wahlID, wahlbezirkID } = to.params;

    if (wahlID === undefined || Array.isArray(wahlID)) {
      return false;
    }
    if (wahlbezirkID === undefined || Array.isArray(wahlbezirkID)) {
      return false;
    }

    return useStatusStore().isStepDone(wahlID, wahlbezirkID, requiredStep);
  }

  return {
    isStepDoneInElectionState,
  };
}
