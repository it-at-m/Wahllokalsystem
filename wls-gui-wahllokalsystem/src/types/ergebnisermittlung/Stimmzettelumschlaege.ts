export interface Stimmzettelumschlaege {
  anzahlWaehler: number;
}

export class StimmzettelumschlaegeBuilder implements Stimmzettelumschlaege {
  constructor(public anzahlWaehler: number) {}

  static create(): Stimmzettelumschlaege {
    return new StimmzettelumschlaegeBuilder(0);
  }

  withAnzahlWaehler(anzahlWaehler: number) {
    this.anzahlWaehler = anzahlWaehler;
    return this;
  }
}
