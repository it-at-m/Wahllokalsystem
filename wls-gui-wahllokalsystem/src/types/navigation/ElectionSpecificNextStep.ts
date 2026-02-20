import type { ElectionWorkflowState } from "@/types/navigation/ElectionWorkflowState.ts";
import type { RouteLocationAsRelativeGeneric } from "vue-router";

export interface ElectionSpecificNextStep {
  getNextRouteOrNull(
    wahlstatus: ElectionWorkflowState
  ): RouteLocationAsRelativeGeneric | null;
}
