import type { RouteLocationAsRelativeGeneric } from "vue-router";

export interface NavigationDefinition {
  title: string;
  targetRoute: RouteLocationAsRelativeGeneric;
  disabled: boolean;
}
