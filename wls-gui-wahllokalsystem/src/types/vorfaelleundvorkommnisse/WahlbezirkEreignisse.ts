import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

export interface WahlbezirkEreignisse {
  wahlbezirkID: string;
  keineVorfaelle?: boolean;
  keineVorkommnisse?: boolean;
  ereigniseintraege?: Array<Ereignis>;
}

export class WahlbezirkEreignisseBuilder implements WahlbezirkEreignisse {
  constructor(
    public wahlbezirkID: string,
    public keineVorfaelle?: boolean,
    public keineVorkommnisse?: boolean,
    public ereigniseintraege?: Ereignis[]
  ) {}

  static createEmptyWahlbezirkEreignisse(): WahlbezirkEreignisseBuilder {
    return new WahlbezirkEreignisseBuilder("", true, true, []);
  }
}
