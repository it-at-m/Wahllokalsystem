import type { Beschlussfassung } from "@/types/dse/beschlussfassung/Beschlussfassung.ts";
import type { Beschlussgrund } from "@/types/dse/beschlussfassung/Beschlussgrund.ts";
import type { Wahlvorschlag } from "@/types/dse/stimmzettelerfassung/Wahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  wahlvorschlaege: Wahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  beschlussvorschlag: Beschlussgrund[];
  beschlussfassung: Beschlussfassung | null;
}
