import type { Beschlussfassung } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Beschlussgrund.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/Wahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  wahlvorschlaege: Wahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  beschlussvorschlag: Beschlussgrund[];
  beschlussfassung: Beschlussfassung | null;
}
