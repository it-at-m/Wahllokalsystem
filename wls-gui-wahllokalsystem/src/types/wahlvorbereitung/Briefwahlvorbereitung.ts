import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

export interface Briefwahlvorbereitung {
  wahlbezirkID: string;
  urneVersiegelt: boolean;
  urnenAnzahl: Wahlurne[];
}
