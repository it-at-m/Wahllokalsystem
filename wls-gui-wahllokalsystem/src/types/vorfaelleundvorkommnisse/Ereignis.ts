import type { EreignisartEnum } from "@/types/vorfaelleundvorkommnisse/Ereignisart.ts";

export interface Ereignis {
  beschreibung?: string;
  uhrzeit?: string;
  ereignisart?: EreignisartEnum;
}

export class EreignisBuilder implements Ereignis {
  constructor(
    public beschreibung?: string,
    public uhrzeit?: string,
    public ereignisart?: EreignisartEnum
  ) {}

  static createMinimal(): EreignisBuilder {
    return new EreignisBuilder();
  }

  static createComplete(): EreignisBuilder {
    return new EreignisBuilder(
      "Ein Stift ist runter gefallen",
      "12:33",
      "VORFALL"
    );
  }

  withBeschreibung(beschreibung: string) {
    this.beschreibung = beschreibung;
    return this;
  }

  withUhrzeit(uhrzeit: string) {
    this.uhrzeit = uhrzeit;
    return this;
  }

  withEreignisart(ereignisart: EreignisartEnum) {
    this.ereignisart = ereignisart;
    return this;
  }
}
