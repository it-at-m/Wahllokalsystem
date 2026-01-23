import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

export interface ElectionSpecificNextStep {
  getNextRouteOrNull(wahlstatus: Status): RouteLocationAsRelativeGeneric | null;
}
