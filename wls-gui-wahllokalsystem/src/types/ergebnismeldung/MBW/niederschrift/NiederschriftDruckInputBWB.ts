import type { NiederschriftDruckInputBase } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBase.ts";

import type { NiederschriftBeanstandeteWahlbriefe } from "./NiederschriftBeanstandeteWahlbriefe";
import type { NiederschriftWahlbriefdaten } from "./NiederschriftWahlbriefdaten";

export interface NiederschriftUhrzeit {
  stunde: string;
  minute: string;
}

export interface NiederschriftDruckInputBWB extends NiederschriftDruckInputBase {
  wahlbriefdaten: NiederschriftWahlbriefdaten;
  beanstandeteWahlbriefe: NiederschriftBeanstandeteWahlbriefe;
}

export class Partei {
  _tabledata: [[]];
  identifikator;
  kurzname;
  ordnungszahl;
  zweitstimmenGesamt;

  // @ts-expect-error old code, will be refactored later
  constructor(identifikator, kurzname, ordnungszahl) {
    this._tabledata = [[]];
    this.identifikator = identifikator;
    this.kurzname = kurzname;
    this.ordnungszahl = ordnungszahl;
    this.zweitstimmenGesamt = 0;
  }

  //@ts-expect-error old code, will be refactored later
  pushKandidat(kandidat) {
    if (kandidat.wahlvorschlagID === this.identifikator) {
      const colNr = parseInt(kandidat.tabellenSpalteInNiederschrift) || 0;
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

  //@ts-expect-error old code, will be refactored later
  _extendTableWidth(numberOfColumns) {
    this._tabledata.forEach((zeile) => {
      // ist eigentlich width - die Breite der Tabelle
      zeile.length = numberOfColumns;
    });
  }

  //@ts-expect-error old code, will be refactored later
  _fillFirstEmptyCellOrCreateRow(colNr, kandidat) {
    let cellEmpty = false;
    let rowNr = 0;
    while (rowNr < this._tabledata.length && !cellEmpty) {
      if (!this._tabledata[rowNr][colNr]) {
        cellEmpty = true;
        // @ts-expect-error old code, will be refactored later
        this._tabledata[rowNr][colNr] = kandidat;
      }
      rowNr++;
    }
    if (!cellEmpty) {
      // alle Zellen in der Spalte colNr aller Zeilen waren belegt.
      // Eine neue Zeile wird benötigt und kreiert
      const newRow = new Array(this._tabledata[0].length);
      newRow[colNr] = kandidat;
      // @ts-expect-error old code, will be refactored later
      this._tabledata.push(newRow);
    }
  }
}
