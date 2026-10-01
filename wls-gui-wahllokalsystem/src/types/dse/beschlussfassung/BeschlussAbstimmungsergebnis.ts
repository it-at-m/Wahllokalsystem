export interface BeschlussAbstimmungsergebnis {
  stimmenDafuer: number | null;
  stimmenDagegen: number | null;
  hasWahlvorsteherVotedDafuer: boolean;
  abstimmungIsUnentschieden: boolean;
  abstimmungIsUngueltig: boolean;
}
