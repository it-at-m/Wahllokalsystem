export default class BroadcastMessageToSend {
  wahlbezirkIDs: string[];
  nachricht: string;

  constructor(wahlbezirkIDs: string[], nachricht: string) {
    this.wahlbezirkIDs = wahlbezirkIDs;
    this.nachricht = nachricht;
  }
}
