export interface Stimmzettelumschlaege {
  anzahlWaehler: number | null;
}

export class StimmzettelumschlaegeBuilder implements Stimmzettelumschlaege {
  constructor(public anzahlWaehler: number | null) {}

  static create(): Stimmzettelumschlaege {
    return new StimmzettelumschlaegeBuilder(null);
  }
}
