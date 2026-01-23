import type { ElectionSpecificNextStep } from "@/types/navigation/ElectionSpecificNextStep.ts";
import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { WahlMetaData } from "@/types/wlsTypes/WahlMetaData.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
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
    if (!useStatusStore().isWahlvorstandErfasst) {
      return routeWithName(ROUTE_WAHLVORSTAND);
    }

    //check all elections in their order
    const { user } = useUserStore();
    const { isElectionFinished } = useStatusStore();
    const metaDataOfFirstUnfinishedElection = user.wahlMetaData.find(
      (wahlMetaData) =>
        !isElectionFinished(wahlMetaData.wahlID, wahlMetaData.wahlbezirkID)
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
    const wahl = useWahlenStore().wahlenActions.getWahlOrUndefinedById(
      metaDataOfFirstUnfinishedElection.wahlID
    );
    if (wahl) {
      const statusOfElection = useStatusStore().getStatus(
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
