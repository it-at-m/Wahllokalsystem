import type { NiederschriftEreignis } from "./NiederschriftEreignis";

export interface NiederschriftEreignisse {
  hasEreignisse: boolean;
  vorfaelle: NiederschriftEreignis[];
  vorkommnisse: NiederschriftEreignis[];
}
