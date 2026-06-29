import type { InputHistoryTypeEnum } from "@/types/experimental/InputHistoryTypeEnum.ts";

export interface InputHistoryItem {
  type: InputHistoryTypeEnum;
  text: string[];
}
