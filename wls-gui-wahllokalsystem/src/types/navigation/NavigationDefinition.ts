import type { MbwRoutesEnum } from "@/types/navigation/MbwRoutesEnum.ts";

export interface NavigationDefinition {
  title: string;
  targetRouteName: MbwRoutesEnum;
  disabled: boolean; //TODO Optional für false ergänzen
}
