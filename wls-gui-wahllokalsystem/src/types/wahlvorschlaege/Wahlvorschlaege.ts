import type { Wahlvorschlag } from "@/types/wahlvorschlaege/Wahlvorschlag.ts";

export interface Wahlvorschlaege {
  wahlID: string;
  wahlbezirkID: string;
  stimmzettelgebietID: string;
  wahlvorschlaege: Wahlvorschlag[];
}
