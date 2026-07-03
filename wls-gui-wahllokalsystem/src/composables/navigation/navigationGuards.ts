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

  const permitNavigationWhenWahlvorstandIsErfasstOrAllElectionsAreFinished: NavigationGuard =
    () =>
      useWorkflowStore().isWahlvorstandErfasst ||
      useWorkflowStore().areAllElectionsFinished;

  const permitNavigationWhenWahleroeffnungIsErfasst: NavigationGuard = () =>
    useWorkflowStore().isWahleroeffnungErfasst;

  const requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb: NavigationGuard =
    () =>
      useUserStore().isBWB ? useWorkflowStore().isWahleroeffnungErfasst : true;

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

  const requiresWahlhandlungErfasstWhenWahlbezirksArtUwb: NavigationGuard =
    () =>
      useUserStore().isUWB ? useWorkflowStore().isWahlhandlungErfasst : true;

  const requiresWahlbriefzulassungErfasstWhenWahlbezirksArtBwb: NavigationGuard =
    () =>
      useUserStore().isBWB
        ? useWorkflowStore().isWahlbriefzulassungErfasst
        : true;

  const requiresStimmabgabevermerkeErfasstWhenWahlbezirksArtUwb: NavigationGuard =
    () =>
      useUserStore().isUWB
        ? useWorkflowStore().isStimmabgabevermerkeErfasst
        : true;

  const requiresAnzahlWahlscheineErfasstWhenWahlbezirksArtBwb: NavigationGuard =
    () =>
      useUserStore().isBWB
        ? useWorkflowStore().isAnzahlWahlscheineErfasst
        : true;

  const requiresIsNachlieferungsbezirk: NavigationGuard = () =>
    useUserStore().isNachlieferungsbezirk;

  const permitNavigationOnlyForWahlbezirksArtUwb: NavigationGuard = () =>
    useUserStore().isUWB;

  const permitNavigationOnlyForWahlbezirksArtBwb: NavigationGuard = () =>
    useUserStore().isBWB;

  const permitNavigationOnlyIfUserIsLoggedOut: NavigationGuard = () =>
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
    permitNavigationWhenWahlvorstandIsErfasstOrAllElectionsAreFinished,
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
    requiresWahleroeffnungErfasstWhenWahlbezirksArtBwb,
    requiresWahlhandlungErfasstWhenWahlbezirksArtUwb,
    requiresWahlbriefzulassungErfasstWhenWahlbezirksArtBwb,
    requiresStimmabgabevermerkeErfasstWhenWahlbezirksArtUwb,
    requiresAnzahlWahlscheineErfasstWhenWahlbezirksArtBwb,
    requiresIsNachlieferungsbezirk,
  };
}
