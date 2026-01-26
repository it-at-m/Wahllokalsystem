import type { AWerte } from "@/types/ergebnismeldung/common/AWerte.ts";
import type { BWerte } from "@/types/ergebnismeldung/common/BWerte.ts";
import type { MbwErgebnisseAndWahlvorschlag } from "@/types/ergebnismeldung/MBW/MbwErgebnisseAndWahlvorschlag.ts";
import type { Wahl } from "@/types/wahl/Wahl.ts";
import type { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

export interface ErgebnismeldungDruckInput {
  sendOk: boolean;
  barcode: string;
  wahlbezirkNummer: string;
  aWerte?: AWerte;
  bWerte: BWerte;
  ungueltigeStimmen: string[];
  gueltigeStimmenGesamt: string[];
  gueltigeStimmenListe: MbwErgebnisseAndWahlvorschlag[];
  alleStimmen: string[];
  alleStimmenErgebnisGesamt?: string;
  footer?: string;
  aktuelleWahl: Wahl;
  wahlbezirksArt: WahlbezirksArtEnum;
}
