import type { ElectionWorkflow } from "@/types/navigation/ElectionWorkflow.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

export interface ElectionSpecificNextStep {
  getNextRouteOrNull(
    wahlstatus: ElectionWorkflow
  ): RouteLocationAsRelativeGeneric | null;
}
