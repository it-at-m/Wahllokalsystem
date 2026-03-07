import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { StimmzettelEventTypeEnum } from "@/types/experimental/StimmzettelEventTypeEnum.ts";

export interface StimmzettelEvent extends AbstractCommandEvent {
  stimmzettelEventType: StimmzettelEventTypeEnum;
}
