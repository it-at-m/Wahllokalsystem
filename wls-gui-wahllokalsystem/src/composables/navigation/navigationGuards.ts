import type {
  NavigationGuard,
  RouteLocationNormalizedGeneric,
} from "vue-router";

import { useUserStore } from "@/stores/userStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";

type NavigationGuardFactory<T> = (options: T) => NavigationGuard;

export function useNavigationGuards() {
  const isStepDoneInElectionState: NavigationGuardFactory<string> =
    (requiredStep) => (to) =>
      _isStepDone(to, requiredStep);

  const permitNavigationWhenWahlumgebungIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlumgebungErfasst;

  const permitNavigationOnlyForWahlbezirksArtUwb = () => useUserStore().isUWB;

  const permitNavigationOnlyForWahlbezirksArtBwb = () => useUserStore().isBWB;

  const permitNavigationOnlyIfUserIsLoggedOut = () =>
    !useUserStore().isUserLoggedIn;

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

    return useWorkflowStore().isStepDone(wahlId, wahlbezirkId, requiredStep);
  }

  return {
    isStepDoneInElectionState,
    permitNavigationWhenWahlumgebungIsErfasst,
    permitNavigationOnlyForWahlbezirksArtUwb,
    permitNavigationOnlyForWahlbezirksArtBwb,
    permitNavigationOnlyIfUserIsLoggedOut,
  };
}
