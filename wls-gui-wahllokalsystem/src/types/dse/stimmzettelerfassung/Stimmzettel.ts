import type { Beschlussfassung } from "@/types/dse/beschlussfassung/Beschlussfassung.ts";
import type { SystemBeschlussgrund } from "@/types/dse/beschlussfassung/SystemBeschlussgrund.ts";
import type { WahlvorstandBeschlussgrund } from "@/types/dse/beschlussfassung/WahlvorstandBeschlussgrund.ts";
import type { DseWahlvorschlag } from "@/types/dse/stimmzettelerfassung/DseWahlvorschlag.ts";

import { StimmzettelGueltigkeitEnum } from "@/types/dse/stimmzettelerfassung/StimmzettelGueltigkeitEnum.ts";

export interface Stimmzettel {
  wahlvorschlaege: DseWahlvorschlag[];

  invalideVotes: number | null;
  gueltigkeit: StimmzettelGueltigkeitEnum;
  wahlvorstandBeschlussvorschlag: WahlvorstandBeschlussgrund[];
  systemBeschlussvorschlag: SystemBeschlussgrund[];
  beschlussfassung: Beschlussfassung | null;
}
