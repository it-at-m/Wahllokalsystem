import type { WahlState } from "@/stores/statusStore.ts";
import type { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

import { ROUTE_WAHLVORSTAND, ROUTES_HOME } from "@/constants.ts";
import { MbwRoutesEnum } from "@/plugins/router/mwbRoutes.ts";
import { useStatusStore } from "@/stores/statusStore.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

interface ElectionSpecificNextStep {
  getNextRoute(wahlstatus: WahlState): RouteLocationAsRelativeGeneric | null;
}

const NullImpl: ElectionSpecificNextStep = {
  getNextRoute(): RouteLocationAsRelativeGeneric | null {
    return null;
  },
};

const MBWNestStepImpl: ElectionSpecificNextStep = {
  getNextRoute(wahlstatus: WahlState): RouteLocationAsRelativeGeneric | null {
    if (!wahlstatus.schnellmeldungGedruckt) {
      return {
        name: MbwRoutesEnum.MBW_AUSZAEHLUNG_STIMMZETTEL,
        params: {
          wahlId: "",
          wahlbezirkId: "",
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
    const firstWahlWithoutNiederschrift =
      useWahlenStore().wahlenState.wahlen?.find((wahl) => {
        const wahlStatus = useStatusStore().getWahl(wahl.wahlID);
        return wahlStatus.value.niederschriftGedruckt;
      });

    if (firstWahlWithoutNiederschrift) {
      const nextHandlerForWahl =
        electionSpecificNextStepHandlers[firstWahlWithoutNiederschrift.wahlart];
      const nextStep = nextHandlerForWahl.getNextRoute(
        useStatusStore().getWahl(firstWahlWithoutNiederschrift.wahlID).value
      );
      if (nextStep) {
        return nextStep;
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
