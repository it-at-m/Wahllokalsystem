import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";

// Minimal input shape for the Niederschrift (only fields used by the template)
export interface NiederschriftWahlvorstandsmitglied {
  nachname: string;
  vorname: string;
  funktionsName: string;
}

export interface NiederschriftEreignis {
  uhrzeit: string;
  beschreibung: string;
}

export interface NiederschriftEreignisse {
  hasEreignisse: boolean;
  vorfaelle: NiederschriftEreignis[];
  vorkommnisse: NiederschriftEreignis[];
}

export interface NiederschriftUhrzeit {
  stunde: string;
  minute: string;
}

export interface NiederschriftGueltigeStimme {
  ordnungszahl: number;
  parteiname: string;
  stapelA: number;
  stapelB: number;
  stapelBC: number;
  gesamt: number;
}

export interface NiederschriftGueltigeStimmenErgebnisGesamt {
  stapelA: number;
  stapelB: number;
  stapelBC: number;
  gesamt: number;
}

export interface NiederschriftWahlbriefdaten {
  wahlbriefe: string | number;
  verzeichnisseUngueltige: string | number;
  nachtraege: string | number;
  nachtraeglichUeberbrachte: number;
}

export interface NiederschriftBeanstandeteWahlbriefe {
  gesamt: number;
  keinGueltigerWahlschein: number;
  keineUnterschrift: number;
  keinStimmzettelumschlag: number;
  nichtVerschlossen: number;
  mehrereStimmzettelumschlaege: number;
  keinAmtlicherStimmzettelumschlag: number;
  loseStimmzettel: number;
  gegenstandImUmschlag: number;
  gefaehrdetWahlgeheimnis: number;
  gesamtMinusZugelassen: number;
  zugelassen: number;
}

export interface NiederschriftParteiMaxCol {
  width1: number;
  width2: number;
  colsum: number;
}

export interface NiederschriftPartei {
  ordnungszahl: number | string;
  kurzname: string;
  direktKandMit00: { laufendeNr: number | string } | null;
  maxcols: NiederschriftParteiMaxCol[];
  _tabledata: any[]; // table rows for candidate data
}

export interface NiederschriftDruckInput {
  // Wahl info (used for title / date)
  aktuelleWahl: Wahl;
  wahltagFormatiert: string;
  // common print fields
  barcode: string;
  wahlbezirkNummer: string;

  // Wahlvorstand
  wahlvorstaende: NiederschriftWahlvorstandsmitglied[];

  // Uhrzeiten
  eroeffnungsuhrzeit: NiederschriftUhrzeit;
  schliessungsuhrzeit: NiederschriftUhrzeit;

  // Wahlbriefdaten / Beanstandungen
  wahlbriefdaten: NiederschriftWahlbriefdaten;
  beanstandeteWahlbriefe: NiederschriftBeanstandeteWahlbriefe;

  // Zählwerte
  anzahlStimmzettel: number | string;
  anzahlWahlscheine: number | string;
  begruendungStimmzettelumschlaege: { grund: string } | null; // TODO noch nicht eingetragen

  // BWerte (used in section 4.1)
  bWerte: BWerte | number;

  // gültige / ungültige Stimmen
  ungueltigeStimmen: string[] | number | null;
  gueltigeStimmenListe: NiederschriftGueltigeStimme[];
  gueltigeStimmenErgebnisGesamt: NiederschriftGueltigeStimmenErgebnisGesamt;

  // Parteien / Kandidaten (used in 4.3)
  parteienListe: NiederschriftPartei[];

  // Ereignisse / Anlagen
  ereignisse: NiederschriftEreignisse;

  footer: any;
}

export class Parteei {
  _tabledata: [[]];
  identifikator;
  kurzname;
  ordnungszahl;
  zweitstimmenGesamt;

  constructor(identifikator, kurzname, ordnungszahl) {
    this._tabledata = [[]];
    this.identifikator = identifikator;
    this.kurzname = kurzname;
    this.ordnungszahl = ordnungszahl;
    this.zweitstimmenGesamt = 0;
  }

  pushKandidat(kandidat) {
    if (kandidat.wahlvorschlagID === this.identifikator) {
      let colNr = parseInt(kandidat.tabellenSpalteInNiederschrift) || 0;
      if (this._tabledata[0].length < colNr) {
        // erweitere alle Zeilen, dass sie so viele Spalten haben
        this._extendTableWidth(colNr);
      }
      kandidat["laufendeNr"] =
        parseInt(this.ordnungszahl) * 100 + parseInt(kandidat.listenposition);
      if (kandidat.direktkandidat) {
        kandidat["show"] = "disabled";
      }
      this._fillFirstEmptyCellOrCreateRow(colNr, kandidat);
      this.zweitstimmenGesamt += parseInt(kandidat.ergebnis) || 0;
      return true;
    } else return false;
  }

  _extendTableWidth(numberOfColumns) {
    this._tabledata.forEach((zeile) => {
      // ist eigentlich width - die Breite der Tabelle
      zeile.length = numberOfColumns;
    });
  }

  _fillFirstEmptyCellOrCreateRow(colNr, kandidat) {
    let cellEmpty = false;
    let rowNr = 0;
    while (rowNr < this._tabledata.length && !cellEmpty) {
      if (!this._tabledata[rowNr][colNr]) {
        cellEmpty = true;
        this._tabledata[rowNr][colNr] = kandidat;
      }
      rowNr++;
    }
    if (!cellEmpty) {
      // alle Zellen in der Spalte colNr aller Zeilen waren belegt.
      // Eine neue Zeile wird benötigt und kreiert
      let newRow = new Array(this._tabledata[0].length);
      newRow[colNr] = kandidat;
      this._tabledata.push(newRow);
    }
  }
}
