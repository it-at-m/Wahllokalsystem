import type { Wahlvorstandsmitglied } from "@/types/wahlvorstand/Wahlvorstandsmitglied";

export interface Wahlvorstand {
  wahlvorstandsmitglieder: Wahlvorstandsmitglied[];
}

export class WahlvorstandBuilder implements Wahlvorstand {
  constructor(public wahlvorstandsmitglieder: Wahlvorstandsmitglied[]) {}

  static createEmptyWahlvorstand(): WahlvorstandBuilder {
    return new WahlvorstandBuilder([]);
  }
}
