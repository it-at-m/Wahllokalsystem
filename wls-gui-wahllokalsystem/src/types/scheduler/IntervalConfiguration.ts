import type { NamedAction } from "@/types/scheduler/NamedAction.ts";

export interface IntervalConfiguration extends NamedAction {
  delay: number;
  runActionAfterRegister?: boolean;
}
