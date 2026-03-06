import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";
import type { KandidatEventTypeEnum } from "@/types/experimental/KandidatEventTypeEnum.ts";

export interface KandidatEvent extends AbstractCommandEvent {
  type: KandidatEventTypeEnum;
  count?: number;
  kandidatNummer: number;
}
