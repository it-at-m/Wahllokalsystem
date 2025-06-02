import type { Wahlurne } from "@/types/wahlvorbereitung/Wahlurne.ts";

export interface Urnenwahlvorbereitung {
  wahlbezirkID: string;
  anzahlWahlkabinen: number;
  anzahlWahltische: number;
  anzahlNebenraeume: number;
  urnenAnzahl: Wahlurne[];
}
