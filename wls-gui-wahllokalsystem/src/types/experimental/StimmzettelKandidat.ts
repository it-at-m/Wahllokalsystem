import type { StimmzettelWahlvorschlag } from "@/types/experimental/StimmzettelWahlvorschlag.ts";

export interface StimmzettelKandidat {
  direktkandidat: boolean;
  einzelbewerber: boolean;
  identifikator: string;
  isDiscarded: boolean;
  listenposition: number;
  name: string;
  votesByVoter: number;
  votesByWahlvorschlag: number;
  wahlvorschlag: StimmzettelWahlvorschlag;
}
