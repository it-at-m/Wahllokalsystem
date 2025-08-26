import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

export interface WahlbezirkEreignisse {
  wahlbezirkID: string;
  keineVorfaelle?: boolean;
  keineVorkommnisse?: boolean;
  ereigniseintraege: Ereignis[];
}

export class WahlbezirkEreignisseBuilder implements WahlbezirkEreignisse {
  constructor(
    public wahlbezirkID: string,
    public ereigniseintraege: Ereignis[],
    public keineVorfaelle?: boolean,
    public keineVorkommnisse?: boolean
  ) {}

  static createEmptyWahlbezirkEreignisse(): WahlbezirkEreignisseBuilder {
    return new WahlbezirkEreignisseBuilder("", [], true, true);
  }
}
