export interface Stimmzettelumschlaege {
  anzahlWaehler: number | null;
  urneneroeffnungsUhrzeit?: Date;
}

export class StimmzettelumschlaegeBuilder implements Stimmzettelumschlaege {
  constructor(
    public anzahlWaehler: number | null,
    public urneneroeffnungsUhrzeit?: Date
  ) {}

  static create(): Stimmzettelumschlaege {
    return new StimmzettelumschlaegeBuilder(null);
  }
}
