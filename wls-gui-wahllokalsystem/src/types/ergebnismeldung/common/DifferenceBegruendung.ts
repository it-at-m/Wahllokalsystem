export interface DifferenceBegruendung {
  wahlId: string;
  begruendung: string;
  isBegruendungValid: boolean;
  anzahlWahlscheineOrStimmabgabevermerke: number | undefined;
  anzahlStimmzettel: number | undefined | null;
}
