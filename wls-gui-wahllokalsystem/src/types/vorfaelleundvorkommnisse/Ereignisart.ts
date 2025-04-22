export const EreignisartEnum = {
  Vorfall: "VORFALL",
  Vorkommnis: "VORKOMMNIS",
} as const;

export type EreignisartEnum =
  (typeof EreignisartEnum)[keyof typeof EreignisartEnum];
