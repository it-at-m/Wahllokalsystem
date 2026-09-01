export interface Kandidat {
  kandidatId: string;
  nennung: number;
  isDiscarded: boolean;
  votesByVoter: number | null;
  invalidVotes: number | null;
  votesByWahlvorschlag: number | null;
}
