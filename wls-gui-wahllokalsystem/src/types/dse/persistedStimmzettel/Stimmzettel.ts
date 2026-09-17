import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedBeschlussfassung } from "@/types/dse/persistedStimmzettel/PersistedBeschlussfassung.ts";
import type { PersistedWahlvorschlag } from "@/types/dse/persistedStimmzettel/PersistedWahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/persistedStimmzettel/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  stimmzettelkennung: number;
  teamID: string;
  wahlvorschlaege: PersistedWahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[];
  systemBeschlussvorschlag: SystemBeschlussgrund[];
  beschlussfassung: PersistedBeschlussfassung | null;
}
