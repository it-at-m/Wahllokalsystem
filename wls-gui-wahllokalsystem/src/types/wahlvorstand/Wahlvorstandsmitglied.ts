import type { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

export interface Wahlvorstandsmitglied {
  identifikator: string; //TODO is always given when reading => Issue 851
  anwesend: boolean;
  familienname: string;
  vorname: string;
  funktion: WahlvorstandsmitgliedFunktionEnum;
  funktionsname?: string;
}
