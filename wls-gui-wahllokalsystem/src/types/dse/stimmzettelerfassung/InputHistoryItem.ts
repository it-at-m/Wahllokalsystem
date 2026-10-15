import type { InputHistoryTypeEnum } from "@/types/dse/stimmzettelerfassung/InputHistoryTypeEnum.ts";

export interface InputHistoryItem {
  type: InputHistoryTypeEnum;
  text: string[];
}
