import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedBeschlussfassung } from "@/types/dse/persistedStimmzettel/PersistedBeschlussfassung.ts";
import type { Wahlvorschlag } from "@/types/dse/persistedStimmzettel/Wahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  teamID: string;
  wahlvorschlaege: Wahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[];
  systemBeschlussvorschlag: SystemBeschlussgrund[];
  beschlussfassung: PersistedBeschlussfassung | null;
}
