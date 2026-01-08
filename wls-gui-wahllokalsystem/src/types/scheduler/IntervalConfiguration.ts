import { NamedAction } from "@/types/scheduler/NamedAction.ts";

export class IntervalConfiguration extends NamedAction {
  delay: number;
  runActionAfterRegister: boolean;

  constructor(
    title: string,
    action: () => void,
    delay: number,
    runActionAfterRegister = false
  ) {
    super(title, action);
    this.delay = delay;
    this.runActionAfterRegister = runActionAfterRegister;
  }
}
