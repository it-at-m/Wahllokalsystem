import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

export interface Urnenwahlvorbereitung {
  wahlbezirkID: string;
  anzahlWahlkabinen: number | null;
  anzahlWahltische: number | null;
  anzahlNebenraeume: number | null;
  urnenAnzahl: Wahlurne[];
}
