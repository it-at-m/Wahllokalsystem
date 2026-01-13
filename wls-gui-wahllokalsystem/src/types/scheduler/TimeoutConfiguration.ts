import type { NamedAction } from "@/types/scheduler/NamedAction.ts";

export interface TimeoutConfiguration extends NamedAction {
  dateOfAction: Date;
}
