import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

export interface Wahlvorbereitung {
  wahlbezirkID: string;
  urneVersiegelt: boolean;
  urnenAnzahl: Wahlurne[];
}
