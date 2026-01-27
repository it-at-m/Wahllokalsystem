export interface DifferenceDialogItem {
  isVisible: boolean;
  wahlId: string;
  begruendung: string;
  isBegruendungValid: boolean;
  anzahlWahlscheineOrStimmabgabevermerke: number | undefined;
  anzahlStimmzettel: number | undefined | null;
}
