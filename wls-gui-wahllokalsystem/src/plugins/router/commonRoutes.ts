import type { RouteRecordRawWithoutName } from "@/types/navigation/RouteRecordRawWithoutName.ts";
import type {
  RouteLocationAsRelativeGeneric,
  RouteRecordRaw,
} from "vue-router";

import { useNavigationGuards } from "@/composables/navigation/navigationGuards.ts";
import { LOGOUT_BY_INACTIVITY_QUERY_PARAMETER } from "@/constants.ts";
import { CommonRoutesEnum } from "@/types/navigation/CommonRoutesEnum.ts";
import LogoutSuccessView from "@/views/LogoutSuccessView.vue";

const { permitNavigationOnlyIfUserIsLoggedOut } = useNavigationGuards();

const commonRoutes: Record<CommonRoutesEnum, RouteRecordRawWithoutName> = {
  [CommonRoutesEnum.LOGOUT]: {
    path: "/logout",
    component: LogoutSuccessView,
    beforeEnter: permitNavigationOnlyIfUserIsLoggedOut,
  },
};

export function createLogoutRoute(
  byInactivity = false
): RouteLocationAsRelativeGeneric {
  const route = {
    name: CommonRoutesEnum.LOGOUT,
  } as RouteLocationAsRelativeGeneric;

  if (byInactivity) {
    route.query = { [LOGOUT_BY_INACTIVITY_QUERY_PARAMETER]: "1" };
  }

  return route;
}

export const commonRouteDefinitions: RouteRecordRaw[] = Object.entries(
  commonRoutes
).map(
  ([routeName, { beforeEnter, component, path }]) =>
    ({
      name: routeName,
      path,
      component,
      beforeEnter,
    }) as RouteRecordRaw
);
