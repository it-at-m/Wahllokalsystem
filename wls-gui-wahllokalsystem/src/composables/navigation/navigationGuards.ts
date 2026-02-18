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

  const permitNavigationWhenWahleroeffnungUWBIsErfasst: NavigationGuard = () =>
    useUserStore().isUWB && useWorkflowStore().isWahleroeffnungErfasst;

  const permitNavigationWhenWahleroeffnungBWBIsErfasst: NavigationGuard = () =>
    useUserStore().isBWB && useWorkflowStore().isWahleroeffnungErfasst;

  const permitNavigationWhenWahlumgebungUWBIsErfasst: NavigationGuard = () =>
    useUserStore().isUWB && useWorkflowStore().isWahlumgebungErfasst;

  const permitNavigationWhenWahlumgebungBWBIsErfasst: NavigationGuard = () =>
    useUserStore().isBWB && useWorkflowStore().isWahlumgebungErfasst;

  const permitNavigationWhenWahlbriefeErfassenIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlbriefeErfassenErfasst;

  const permitNavigationWhenWahlbriefeZulassenIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahlbriefeZulassenErfasst;

  const permitNavigationWhenWaehlerverzeichnisIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWaehlerverzeichnisErfasst;

  const permitNavigationWhenStimmabgabeIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isStimmabgabeErfasst;

  const permitNavigationOnlyForWahlbezirksArtUwb: NavigationGuard = () =>
    useUserStore().isUWB;

  const permitNavigationOnlyForWahlbezirksArtBwb: NavigationGuard = () =>
    useUserStore().isBWB;

  const permitNavigationOnlyIfUserIsLoggedOut: NavigationGuard = () =>
    !useUserStore().isUserLoggedIn;

  const beforeEnterBeginnStimmabgabe: NavigationGuard = () =>
    (useUserStore().isUWB &&
      useWorkflowStore().isWahlvorstandErfasst &&
      useWorkflowStore().isWahlumgebungErfasst &&
      useWorkflowStore().isWaehlerverzeichnisErfasst) ||
    (useUserStore().isBWB && useWorkflowStore().isWahlvorstandErfasst);

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
    permitNavigationWhenWahleroeffnungUWBIsErfasst,
    permitNavigationWhenWahleroeffnungBWBIsErfasst,
    permitNavigationWhenWahlumgebungUWBIsErfasst,
    permitNavigationWhenWahlumgebungBWBIsErfasst,
    permitNavigationWhenWahlbriefeErfassenIsErfasst,
    permitNavigationWhenWahlbriefeZulassenIsErfasst,
    permitNavigationWhenWaehlerverzeichnisIsErfasst,
    permitNavigationWhenStimmabgabeIsErfasst,
    permitNavigationOnlyForWahlbezirksArtUwb,
    permitNavigationOnlyForWahlbezirksArtBwb,
    permitNavigationOnlyIfUserIsLoggedOut,
    beforeEnterBeginnStimmabgabe,
    beforeEnterWahlumgebung,
  };
}
