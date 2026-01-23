import type { Status } from "@/types/ergebnismeldung/common/Status.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

export interface ElectionSpecificNextStep {
  getNextRoute(wahlstatus: Status): RouteLocationAsRelativeGeneric | null;
}
