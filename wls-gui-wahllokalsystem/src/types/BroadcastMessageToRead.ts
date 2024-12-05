export default class BroadcastMessageToRead {
  oid: string;
  wahlbezirkIDs: string[];
  nachricht: string;

  constructor(oid: string, wahlbezirkIDs: string[], nachricht: string) {
    this.oid = oid;
    this.wahlbezirkIDs = wahlbezirkIDs;
    this.nachricht = nachricht;
  }
}
