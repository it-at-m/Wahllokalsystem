import type { RouteLocationAsRelativeGeneric } from "vue-router";

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

  return {
    routeWithName,
    routeWithNameAndParams,
  };
}
