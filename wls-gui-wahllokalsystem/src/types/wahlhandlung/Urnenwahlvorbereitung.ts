import type { Wahlvorbereitung } from "@/types/wahlhandlung/Wahlvorbereitung.ts";

export interface Urnenwahlvorbereitung extends Wahlvorbereitung {
  anzahlWahlkabinen: number | null;
  anzahlWahltische: number | null;
  anzahlNebenraeume: number | null;
}
