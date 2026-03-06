import type { AbstractCommandEvent } from "@/types/experimental/AbstractCommandEvent.ts";

import { WahlvorschlagEventTypeEnum } from "@/types/experimental/WahlvorschlagEventTypeEnum.ts";

export interface WahlvorschlagEvent extends AbstractCommandEvent {
  type: WahlvorschlagEventTypeEnum;
  wahlvorschlagOrdnungszahl: number;
}
