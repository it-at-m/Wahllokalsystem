import type { PersistedStimmzettel } from "@/types/dse/stimmzettelerfassung/PersistedStimmzettel.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface BeschlussentscheidungenDruckInput {
  stimmzettelWithBeschluss: PersistedStimmzettel[];
  wahlbezirkNummer: string;
  aktuelleWahl: Wahl;
  wahlbezirksArt: WahlbezirksArtEnum;
  footer?: string;
}
