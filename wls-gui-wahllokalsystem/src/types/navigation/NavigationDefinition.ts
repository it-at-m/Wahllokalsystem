import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";

export interface NavigationDefinition {
  title: string;
  targetRoute: RouteLocationAsRelativeGenericWithStringName;
  disabled: boolean;
}
