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

  const permitNavigationWhenWahlvorstandIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlvorstandErfasst;

  const permitNavigationWhenWahleroeffnungIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahleroeffnungErfasst;

  const permitNavigationWhenWahlumgebungIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlumgebungErfasst;

  const requiresWahlumgebungErfasstWhenWahlbezirksArtUwb: NavigationGuard =
    () =>
      useUserStore().isUWB ? useWorkflowStore().isWahlumgebungErfasst : true;

  const permitNavigationWhenWahlbriefeErfassenIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlbriefeErfassenErfasst;

  const permitNavigationWhenWahlbriefeZulassenIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlbriefeZulassenErfasst;

  const permitNavigationWhenWaehlerverzeichnisIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWaehlerverzeichnisErfasst;

  const requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb: NavigationGuard =
    () =>
      useUserStore().isUWB
        ? useWorkflowStore().isWaehlerverzeichnisErfasst
        : true;

  const permitNavigationWhenStimmabgabeIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isStimmabgabeErfasst;

  const permitNavigationOnlyForWahlbezirksArtUwb: NavigationGuard = () =>
    useUserStore().isUWB;

  const permitNavigationOnlyForWahlbezirksArtBwb: NavigationGuard = () =>
    useUserStore().isBWB;

  const permitNavigationOnlyIfUserIsLoggedOut: NavigationGuard = () =>
    !useUserStore().isUserLoggedIn;

  const beforeEnterWahlumgebung: NavigationGuard = () =>
    (useUserStore().isUWB && useWorkflowStore().isWahlvorstandErfasst) ||
    (useUserStore().isBWB &&
      useWorkflowStore().isWahlvorstandErfasst &&
      useWorkflowStore().isWahleroeffnungErfasst);

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
    permitNavigationWhenWahlvorstandIsErfasst,
    permitNavigationWhenWahleroeffnungIsErfasst,
    permitNavigationWhenWahlumgebungIsErfasst,
    permitNavigationWhenWahlbriefeErfassenIsErfasst,
    permitNavigationWhenWahlbriefeZulassenIsErfasst,
    permitNavigationWhenWaehlerverzeichnisIsErfasst,
    permitNavigationWhenStimmabgabeIsErfasst,
    permitNavigationOnlyForWahlbezirksArtUwb,
    permitNavigationOnlyForWahlbezirksArtBwb,
    permitNavigationOnlyIfUserIsLoggedOut,
    requiresWahlumgebungErfasstWhenWahlbezirksArtUwb,
    requiresWaehlerverzeichnisErfasstWhenWahlbezirksArtUwb,
    beforeEnterWahlumgebung,
  };
}
