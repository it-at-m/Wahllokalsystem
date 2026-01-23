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
    const { wahlId, wahlbezirkId } = to.params;

    if (wahlId === undefined || Array.isArray(wahlId)) {
      return false;
    }
    if (wahlbezirkId === undefined || Array.isArray(wahlbezirkId)) {
      return false;
    }

    return useStatusStore().isStepDone(wahlId, wahlbezirkId, requiredStep);
  }

  return {
    isStepDoneInElectionState,
  };
}
