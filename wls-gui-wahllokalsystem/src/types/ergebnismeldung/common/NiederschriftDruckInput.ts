import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MeldungsartEnum } from "@/types/ergebnismeldung/common/MeldungsartEnum.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface NiederschriftWahlvorstandsmitglied {
  nachname?: string;
  vorname?: string;
  funktionsName?: string;
}

export interface NiederschriftEreignisse {
  hasEreignisse?: boolean;
  vorfaelle?: any[];
  vorkommnisse?: any[];
}

export interface NiederschriftUhrzeit {
  stunde?: string;
  minute?: string;
}

export interface NiederschriftGueltigeStimme {
  ordnungszahl?: number;
  bewerbername?: string;
  parteiname?: string;
  wahlvorschlag?: string;
  stapelA?: number;
  stapelB?: number;
  stapelBC?: number;
  gesamt?: number;
}

export interface NiederschriftGueltigeStimmenErgebnisGesamt {
  stapelA: number;
  stapelB: number;
  stapelBC: number;
  gesamt: number;
}

export interface NiederschriftDruckInput {
  meldungsArt: MeldungsartEnum;
  wahlbezirksArt: WahlbezirksArtEnum;
  aktuelleWahl: Wahl;

  // common print fields
  sendOk: boolean;
  barcode: string;
  wahlbezirkNummer: string;
  footer?: string;

  // AWerte / BWerte
  aWerte?: AWerte | number;
  a1?: number;
  a2?: number | null;
  bWerte?: BWerte | number;
  b1?: number;

  // Stimmen / Ergebnisse
  ungueltigeStimmen?: string[] | number | null;
  gueltigeStimmenGesamt?: string[] | number | null;
  gueltigeStimmenListe?:
    | MbwErgebnisseAndWahlvorschlag[]
    | NiederschriftGueltigeStimme[];
  gueltigeStimmenErgebnisGesamt?: NiederschriftGueltigeStimmenErgebnisGesamt;
  alleStimmen?: string[] | number[];
  alleStimmenErgebnisGesamt?: string;

  // Wahlvorstand, Ereignisse, Vorbereitungen
  wahlvorstaende?: NiederschriftWahlvorstandsmitglied[];
  ereignisse?: NiederschriftEreignisse;
  anzahlStimmzettel?: number | string;
  anzahlStimmabgabevermerke?: number | string;
  anzahlWahlscheine?: number | string;
  begruendungStimmzettelumschlaege?: { grund?: string } | null;
  begruendungStapelAB?: any;

  // Uhrzeiten, WVZ, Raum-Ausstattung
  eroeffnungsuhrzeit?: NiederschriftUhrzeit;
  schliessungsuhrzeit?: NiederschriftUhrzeit;
  wvz?: any;
  anzahlWahltische?: number;
  anzahlWahlkabinen?: number;
  anzahlNebenraume?: number;

  // optional list of party names (used in templates)
  parteienListe?: string[];
}
