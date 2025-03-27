import type { Ereignis } from "@/types/vorfaelleundvorkommnisse/Ereignis.ts";

export interface EreignisseWrite {
  ereigniseintraege?: Array<Ereignis>;
}

export class EreignisseWriteBuilder implements EreignisseWrite {
  constructor(public ereigniseintraege: Ereignis[]) {}

  static createEmptyEreignisseWrite(): EreignisseWriteBuilder {
    return new EreignisseWriteBuilder([]);
  }
}
