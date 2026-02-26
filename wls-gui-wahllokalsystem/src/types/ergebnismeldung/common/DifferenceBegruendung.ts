export interface DifferenceBegruendung {
  wahlId: string;
  begruendung: string;
  isBegruendungValid: boolean;
  anzahlWahlscheineOrStimmabgabevermerke: number | null | undefined;
  anzahlStimmzettel: number | undefined | null;
}
