export const StimmzettelGueltigkeitEnum = {
  Valid: "VALID",
  Invalid: "INVALID",
  BeschlussAusstehend: "BESCHLUSS_AUSSTEHEND",
  PseudStimmzettelWegenFehlend: "PSEUD_STIMMZETTEL_WEGEN_FEHLEND",
  Leer: "LEER",
} as const;
export type StimmzettelGueltigkeitEnum =
  (typeof StimmzettelGueltigkeitEnum)[keyof typeof StimmzettelGueltigkeitEnum];
