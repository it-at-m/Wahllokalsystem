import type { WahlvorstandsmitgliedFunktionEnum } from "@/types/wahlvorstand/WahlvorstandsmitgliedFunktion";

export interface Wahlvorstandsmitglied {
  identifikator: string; //TODO is always given when reading => Issue 851
  anwesend: boolean;
  familienname?: string;
  vorname?: string;
  funktion?: WahlvorstandsmitgliedFunktionEnum;
  funktionsname?: string;
}

export class WahlvorstandsmitgliedBuilder implements Wahlvorstandsmitglied {
  constructor(
    public identifikator: string, //TODO is always given when reading => Issue 851
    public anwesend: boolean,
    public familienname?: string,
    public vorname?: string,
    public funktion?: WahlvorstandsmitgliedFunktionEnum,
    public funktionsname?: string
  ) {}

  static createMinimal(): WahlvorstandsmitgliedBuilder {
    return new WahlvorstandsmitgliedBuilder("", false);
  }

  static createComplete(): WahlvorstandsmitgliedBuilder {
    return new WahlvorstandsmitgliedBuilder(
      "",
      true,
      "famname",
      "vorname",
      "SB",
      "funktion"
    );
  }

  withFunktion(
    funktion: WahlvorstandsmitgliedFunktionEnum
  ): WahlvorstandsmitgliedBuilder {
    this.funktion = funktion;
    return this;
  }

  withAnwesend(anwesend: boolean): WahlvorstandsmitgliedBuilder {
    this.anwesend = anwesend;
    return this;
  }

  withIdentifikator(identifikator: string): WahlvorstandsmitgliedBuilder {
    this.identifikator = identifikator;
    return this;
  }

  withFamilienname(familienname: string) {
    this.familienname = familienname;
    return this;
  }

  withVorname(vorname: string) {
    this.vorname = vorname;
    return this;
  }
}
