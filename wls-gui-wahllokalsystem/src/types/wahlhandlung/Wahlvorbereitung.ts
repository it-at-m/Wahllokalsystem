import type { Wahlurne } from "@/types/wahlhandlung/Wahlurne.ts";

export interface Wahlvorbereitung {
  wahlbezirkID: string;
  urneVersiegelt: boolean;
  urnenAnzahl: Wahlurne[];
}
