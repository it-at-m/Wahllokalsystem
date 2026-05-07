import type { NiederschriftUhrzeit } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBWB.ts";
import type { NiederschriftEreignisse } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftEreignisse.ts";
import type { NiederschriftGueltigeStimme } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimme.ts";
import type { NiederschriftGueltigeStimmenErgebnisGesamt } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftGueltigeStimmenErgebnisGesamt.ts";
import type { NiederschriftWahlvorstandsmitglied } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWahlvorstandsmitglied.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

export interface NiederschriftDruckInputBase {
  aktuelleWahl: Wahl;
  wahltagFormatiert: string;
  barcode: string;
  wahlbezirkNummer: string;
  wahlvorstaende: NiederschriftWahlvorstandsmitglied[];
  eroeffnungsuhrzeit: NiederschriftUhrzeit;
  schliessungsuhrzeit: NiederschriftUhrzeit;
  anzahlStimmzettel: number;
  anzahlWahlscheine: number;
  begruendungStimmzettelumschlaege: { grund: string } | null;
  bWerte: number;
  ungueltigeStimmen: string[] | number | null;
  gueltigeStimmenListe: NiederschriftGueltigeStimme[];
  gueltigeStimmenErgebnisGesamt: NiederschriftGueltigeStimmenErgebnisGesamt;
  // eslint-disable-next-line
  parteienListe: any;
  ereignisse: NiederschriftEreignisse;
  footer?: string;
}
