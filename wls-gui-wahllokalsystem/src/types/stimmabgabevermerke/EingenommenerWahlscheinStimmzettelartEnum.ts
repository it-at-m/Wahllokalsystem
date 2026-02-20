export const EingenommenerWahlscheinStimmzettelartEnum = {
  Gross: "GROSS",
  Klein: "KLEIN",
  Beide: "BEIDE",
} as const;

export type EingenommenerWahlscheinStimmzettelartEnum =
  (typeof EingenommenerWahlscheinStimmzettelartEnum)[keyof typeof EingenommenerWahlscheinStimmzettelartEnum];
