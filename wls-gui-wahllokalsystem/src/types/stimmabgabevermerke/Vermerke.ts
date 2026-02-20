import type { Stimmzettel } from "@/types/stimmabgabevermerke/Stimmzettel.ts";

export interface Vermerke {
  blattnummer: number;
  stimmzettel: Stimmzettel[];
}
