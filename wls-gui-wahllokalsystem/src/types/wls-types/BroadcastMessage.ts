export interface BroadcastMessageToRead {
  readonly oid: string;
  readonly wahlbezirkIDs: string[];
  readonly nachricht: string;
}

export class BroadcastMessageToSend {
  wahlbezirkIDs: string[];
  nachricht: string;

  constructor(wahlbezirkIDs: string[], nachricht: string) {
    this.wahlbezirkIDs = wahlbezirkIDs;
    this.nachricht = nachricht;
  }
}
