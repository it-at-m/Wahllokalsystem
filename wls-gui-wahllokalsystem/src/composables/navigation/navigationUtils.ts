import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import {
  ROUTE_WAHLUMGEBUNG,
  ROUTE_WAHLVORSTAND,
  ROUTES_HOME,
} from "@/constants.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { useWorkflowStore } from "@/stores/workflowStore.ts";
import {
  MBWNextStepImpl,
  NullNextStepImpl,
} from "@/types/navigation/NextStepImplConstants.ts";

const electionSpecificNextStepHandlers: Record<
  WahlWahlartEnum,
  ElectionSpecificNextStep
> = {
  BAW: NullNextStepImpl,
  BEB: NullNextStepImpl,
  BTW: NullNextStepImpl,
  BZW: NullNextStepImpl,
  EUW: NullNextStepImpl,
  LTW: NullNextStepImpl,
  MBW: MBWNextStepImpl,
  OBW: NullNextStepImpl,
  SRW: NullNextStepImpl,
  SVW: NullNextStepImpl,
  VE: NullNextStepImpl,
};

export function useNavigationUtils() {
  const workflowStore = useWorkflowStore();
  const wahlenStore = useWahlenStore();
  const userStore = useUserStore();

  function routeWithName(routeName: string): RouteLocationAsRelativeGeneric {
    return {
      name: routeName,
    };
  }

  function routeWithNameAndParams(
    routeName: string,
    params: Record<string, string>
  ): RouteLocationAsRelativeGeneric {
    return {
      name: routeName,
      params,
    };
  }

  function getNextRoute(): RouteLocationAsRelativeGeneric {
    //check if a non election specific step is next
    if (!workflowStore.isWahlvorstandErfasst) {
      return routeWithName(ROUTE_WAHLVORSTAND);
    }

    // check wahlbriefzulassung steps (BWB)
    if (userStore.isBWB && !workflowStore.isWahlumgebungErfasst) {
      return routeWithName(ROUTE_WAHLUMGEBUNG);
    }

    //check all elections in their order
    const metaDataOfFirstUnfinishedElection = userStore.user.wahlMetaData.find(
      (wahlMetaData) =>
        !workflowStore.isElectionFinished(
          wahlMetaData.wahlID,
          wahlMetaData.wahlbezirkID
        )
    );

    if (metaDataOfFirstUnfinishedElection) {
      const nextStepOfElection = _getNextStepOfElection(
        metaDataOfFirstUnfinishedElection
      );
      if (nextStepOfElection) {
        return nextStepOfElection;
      }
    }

    return routeWithName(ROUTES_HOME);
  }

  function _getNextStepOfElection(
    metaDataOfFirstUnfinishedElection: WahlMetaData
  ): RouteLocationAsRelativeGeneric | null {
    const wahl = wahlenStore.wahlenActions.getWahlOrUndefinedById(
      metaDataOfFirstUnfinishedElection.wahlID
    );
    if (wahl) {
      const statusOfElection = useWorkflowStore().getElectionWorkflowState(
        metaDataOfFirstUnfinishedElection.wahlID,
        metaDataOfFirstUnfinishedElection.wahlbezirkID
      );
      if (!statusOfElection) {
        return null;
      }

      const nextHandlerForWahl = electionSpecificNextStepHandlers[wahl.wahlart];
      return nextHandlerForWahl.getNextRouteOrNull(statusOfElection);
    } else {
      return null;
    }
  }

  return {
    routeWithName,
    routeWithNameAndParams,
    getNextRoute,
  };
}
