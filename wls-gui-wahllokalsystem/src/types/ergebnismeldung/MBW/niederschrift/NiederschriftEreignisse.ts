import type { NiederschriftEreignis } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftEreignis.ts";

export interface NiederschriftEreignisse {
  hasEreignisse: boolean;
  vorfaelle: NiederschriftEreignis[];
  vorkommnisse: NiederschriftEreignis[];
}
