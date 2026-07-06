import type { RouteLocationAsRelativeGenericWithStringName } from "@/types/navigation/RouteLocationAsRelativeGenericWithStringName.ts";

export interface ElectionSpecificNextStep {
  getNextRouteOrNull(
    wahlID: string,
    wahlbezirkID: string
  ): RouteLocationAsRelativeGenericWithStringName | null;
}
