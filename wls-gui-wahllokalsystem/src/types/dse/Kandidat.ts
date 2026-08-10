export interface Kandidat {
  kandidatId: string;
  listenposition: number;
  nennung: number;
  isDiscarded: boolean;
  votesByVoter: number | null;
  invalidVotes: number | null;
  votesByWahlvorschlag: number | null;
}
