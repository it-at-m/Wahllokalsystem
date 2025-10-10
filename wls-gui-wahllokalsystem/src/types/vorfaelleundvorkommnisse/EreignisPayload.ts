export interface EreignisPayload {
  dateStr?: string;
  timeStr?: string;
  beschreibung?: string;
}

export class EreignisBuilder implements EreignisPayload {
  constructor(
    public dateStr?: string,
    public timeStr?: string,
    public beschreibung?: string
  ) {}

  static createMinimal(): EreignisBuilder {
    return new EreignisBuilder();
  }

  static createComplete(): EreignisBuilder {
    return new EreignisBuilder("31.09.2025", "15:00", "Beschreibung");
  }

  withBeschreibung(beschreibung: string) {
    this.beschreibung = beschreibung;
    return this;
  }

  withDateOnly(dateOnly: string) {
    this.dateStr = dateOnly;
    return this;
  }

  withTimeOnly(timeOnly: string) {
    this.timeStr = timeOnly;
    return this;
  }
}
