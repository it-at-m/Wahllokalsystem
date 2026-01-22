import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useUserStore } from "@/stores/userStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";
import { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

interface ElectionSpecificNextStep {
  getNextRoute(wahlstatus: Status): RouteLocationAsRelativeGeneric | null;
}

const NullImpl: ElectionSpecificNextStep = {
  getNextRoute(): RouteLocationAsRelativeGeneric | null {
    return null;
  },
};

const MBWNestStepImpl: ElectionSpecificNextStep = {
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

const electionSpecificNextStepHandlers: Record<
  WahlWahlartEnum,
  ElectionSpecificNextStep
> = {
  BAW: NullImpl,
  BEB: NullImpl,
  BTW: NullImpl,
  BZW: NullImpl,
  EUW: NullImpl,
  LTW: NullImpl,
  MBW: MBWNestStepImpl,
  OBW: NullImpl,
  SRW: NullImpl,
  SVW: NullImpl,
  VE: NullImpl,
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
    if (!useStatusStore().isWahlvorstandErfasst) {
      return routeWithName(ROUTE_WAHLVORSTAND);
    }

    //check all elections in their order
    const firstWahlWithoutNiederschrift = useUserStore().user.wahlMetaData.find(
      (wahlMetaData) => {
        const status = useStatusStore().getOrInitStatus(
          wahlMetaData.wahlID,
          wahlMetaData.wahlbezirkID
        );
        return !status.niederschrift.gedruckt;
      }
    );

    if (firstWahlWithoutNiederschrift) {
      const wahl = useWahlenStore().wahlenActions.getWahlOrUndefinedById(
        firstWahlWithoutNiederschrift.wahlID
      );
      if (wahl) {
        const nextHandlerForWahl =
          electionSpecificNextStepHandlers[wahl.wahlart];
        const nextStep = nextHandlerForWahl.getNextRoute(
          useStatusStore().getOrInitStatus(
            firstWahlWithoutNiederschrift.wahlID,
            firstWahlWithoutNiederschrift.wahlbezirkID
          )
        );
        if (nextStep) {
          return nextStep;
        }
      }
    }

    return routeWithName(ROUTES_HOME);
  }

  return {
    routeWithName,
    routeWithNameAndParams,
    getNextRoute,
  };
}
