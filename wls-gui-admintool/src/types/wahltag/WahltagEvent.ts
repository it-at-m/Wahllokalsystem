export interface WahltagEvent {
  wahltagID: string;
  beschreibung: string;
  nummer: string;
}

export const compareByNummerAsc = (
  event1: WahltagEvent,
  event2: WahltagEvent
) => event1.nummer.localeCompare(event2.nummer);
