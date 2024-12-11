export class BroadcastMessageToRead {
  oid: string;
  wahlbezirkIDs: string[];
  nachricht: string;

  constructor(oid: string, wahlbezirkIDs: string[], nachricht: string) {
    this.oid = oid;
    this.wahlbezirkIDs = wahlbezirkIDs;
    this.nachricht = nachricht;
  }
}

export class BroadcastMessageToSend {
  wahlbezirkIDs: string[];
  nachricht: string;

  constructor(wahlbezirkIDs: string[], nachricht: string) {
    this.wahlbezirkIDs = wahlbezirkIDs;
    this.nachricht = nachricht;
  }
}
