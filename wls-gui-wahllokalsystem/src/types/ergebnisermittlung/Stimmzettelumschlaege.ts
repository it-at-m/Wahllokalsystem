export interface Stimmzettelumschlaege {
  anzahlWaehler: number | null;
  urneneroeffnungsUhrzeit: Date | undefined;
}

export class StimmzettelumschlaegeBuilder implements Stimmzettelumschlaege {
  constructor(
    public anzahlWaehler: number | null,
    public urneneroeffnungsUhrzeit: Date | undefined
  ) {}

  static create(): Stimmzettelumschlaege {
    return new StimmzettelumschlaegeBuilder(null, undefined);
  }
}
