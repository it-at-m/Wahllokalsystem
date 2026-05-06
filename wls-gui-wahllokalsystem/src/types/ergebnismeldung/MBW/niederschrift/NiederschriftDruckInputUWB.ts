import type { NiederschriftDruckInputBase } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftDruckInputBase.ts";
import type { NiederschriftWaehlerverzeichnis } from "@/types/ergebnismeldung/MBW/niederschrift/NiederschriftWaehlerverzeichnis.ts";

export interface NiederschriftDruckInputUWB extends NiederschriftDruckInputBase {
  anzahlStimmabgabevermerke: number;
  anzahlWahltische: number;
  wvz: NiederschriftWaehlerverzeichnis;
  a1: number;
  a2: number;
  aWerte: number;
  b1: number;
  bWerte: number;
}
