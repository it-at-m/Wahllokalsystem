export const StimmzettelGueltigkeitEnum = {
  Valid: "VALID",
  Invalid: "INVALID",
  BeschlussAusstehend: "BESCHLUSS_AUSSTEHEND",
  BwbPseudoStimmzettelLeererUmschlag: "BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG",
  Leer: "LEER",
} as const;
export type StimmzettelGueltigkeitEnum =
  (typeof StimmzettelGueltigkeitEnum)[keyof typeof StimmzettelGueltigkeitEnum];
