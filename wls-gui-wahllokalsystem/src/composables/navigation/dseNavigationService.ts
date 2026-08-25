import type { NavigationDefinition } from "@/types/navigation/NavigationDefinition.ts";
import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";
import type { ComputedRef } from "vue";

import { computed } from "vue";

import { DseStepsEnum } from "@/types/navigation/DseStepsEnum.ts";

export function useDseNavigationService(wahlID: string, wahlbezirkID: string) {
  const navigation: ComputedRef<NavigationDefinition[]> = computed(() => {
    return [
      {
        title: `Stimmzettelerfassung`,
        targetRoute: _createRoute(
          DseStepsEnum.DSE_STIMMZETTELERFASSUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Monitoring`,
        targetRoute: _createRoute(
          DseStepsEnum.DSE_MONITORING,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
      {
        title: `Beschlussfassung`,
        targetRoute: _createRoute(
          DseStepsEnum.DSE_BESCHLUSSFASSUNG,
          wahlID,
          wahlbezirkID
        ),
        disabled: false,
      },
    ];
  });

  function _createRoute(
    routeName: DseStepsEnum,
    wahlId: string,
    wahlbezirkId: string
  ): RouteLocationAsRelativeGenericWithStringName {
    return {
      name: routeName,
      params: {
        wahlId,
        wahlbezirkId,
      },
    };
  }

  return {
    navigation,
  };
}
