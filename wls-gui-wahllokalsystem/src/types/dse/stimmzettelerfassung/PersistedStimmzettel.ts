import type { PersistedBeschlussfassung } from "@/types/dse/beschlussfassung/PersistedBeschlussfassung.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { PersistedWahlvorschlag } from "@/types/dse/stimmzettelerfassung/PersistedWahlvorschlag.ts";

import { PersistedStimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettelGueltigkeitEnum.ts";

export interface PersistedStimmzettel {
  stimmzettelkennung: number;
  teamID: string;
  wahlvorschlaege: PersistedWahlvorschlag[];

  invalideVotes: number;
  gueltigkeit: PersistedStimmzettelGueltigkeitEnum;
  wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[];
  systemBeschlussvorschlag: SystemBeschlussgrund[];
  beschlussfassung: PersistedBeschlussfassung | null;
}
