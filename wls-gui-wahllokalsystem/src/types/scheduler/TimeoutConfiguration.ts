import { NamedAction } from "@/types/scheduler/NamedAction.ts";

export class TimeoutConfiguration extends NamedAction {
  dateOfAction: Date;

  constructor(title: string, action: () => void, dateOfAction: Date) {
    super(title, action);
    this.dateOfAction = dateOfAction;
  }
}
