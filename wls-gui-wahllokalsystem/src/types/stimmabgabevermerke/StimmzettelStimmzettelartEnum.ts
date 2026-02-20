export const StimmzettelStimmzettelartEnum = {
  Gross: "GROSS",
  Klein: "KLEIN",
  Beide: "BEIDE",
} as const;

export type StimmzettelStimmzettelartEnum =
  (typeof StimmzettelStimmzettelartEnum)[keyof typeof StimmzettelStimmzettelartEnum];
