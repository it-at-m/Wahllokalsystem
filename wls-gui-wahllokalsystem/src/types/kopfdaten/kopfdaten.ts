import type { KopfdatenStimmzettelgebietsartEnum } from "@/types/kopfdaten/KopfdatenStimmzettelgebietsartEnum.ts";

export interface Kopfdaten {
  wahlID: string;
  wahlbezirkID: string;
  gemeinde: string;
  stimmzettelgebietsart: KopfdatenStimmzettelgebietsartEnum;
  stimmzettelgebietsnummer: string;
  stimmzettelgebietsname: string;
  wahlname: string;
  wahlbezirknummer: string;
  maximalErlaubteStimmenProWaehler?: number | null;
}
