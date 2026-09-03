import type { Beschlussfassung } from "@/types/dse/persistedStimmzettel/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/persistedStimmzettel/Beschlussgrund.ts";
import type { Wahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  teamID: string;
  wahlvorschlaege: Wahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  beschlussvorschlag: Beschlussgrund[];
  beschlussfassung: Beschlussfassung | null;
}
