import type { Beschlussfassung } from "@/types/dse/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/Beschlussgrund.ts";
import type { Wahlvorschlag } from "@/types/dse/Wahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  wahlvorschlaege: Wahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum | null;
  beschlussvorschlag: Beschlussgrund[];
  beschlussfassung: Beschlussfassung | null;
}
