export interface Stimmzettelumschlaege {
  anzahlWaehler: number | undefined;
}

export class StimmzettelumschlaegeBuilder implements Stimmzettelumschlaege {
  constructor(public anzahlWaehler: number | undefined) {}

  static create(): Stimmzettelumschlaege {
    return new StimmzettelumschlaegeBuilder(undefined);
  }

  withAnzahlWaehler(anzahlWaehler: number) {
    this.anzahlWaehler = anzahlWaehler;
    return this;
  }
}
