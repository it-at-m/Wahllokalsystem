import type { WahlvorstandBeschlussvorschlaegeEnum } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussvorschlaegeEnum.ts";

export interface WahlvorstandBeschlussgrund {
  text: string | WahlvorstandBeschlussvorschlaegeEnum;
}
