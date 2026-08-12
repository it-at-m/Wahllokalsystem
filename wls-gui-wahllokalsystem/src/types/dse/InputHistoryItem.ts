import type { InputHistoryTypeEnum } from "@/types/dse/InputHistoryTypeEnum.ts";

export interface InputHistoryItem {
  type: InputHistoryTypeEnum;
  text: string[];
}
