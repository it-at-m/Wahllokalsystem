export const StimmzettelGueltigkeitEnum = {
  Valid: "VALID",
  Invalid: "INVALID",
  BeschlussAusstehend: "BESCHLUSS_AUSSTEHEND",
  BwbPseudoStimmzettelLeererUmschlag: "BWB_PSEUDO_STIMMZETTEL_LEERER_UMSCHLAG",
  Leer: "LEER",
} as const;
export type StimmzettelGueltigkeitEnum =
  (typeof StimmzettelGueltigkeitEnum)[keyof typeof StimmzettelGueltigkeitEnum];

const stimmzettelUngueltigkeit: Record<StimmzettelGueltigkeitEnum, boolean> = {
  [StimmzettelGueltigkeitEnum.Valid]: false,
  [StimmzettelGueltigkeitEnum.Invalid]: true,
  [StimmzettelGueltigkeitEnum.BeschlussAusstehend]: false,
  [StimmzettelGueltigkeitEnum.BwbPseudoStimmzettelLeererUmschlag]: true,
  [StimmzettelGueltigkeitEnum.Leer]: true,
};

export function isStimmzettelUngueltig(
  gueltigkeit: StimmzettelGueltigkeitEnum
) {
  return stimmzettelUngueltigkeit[gueltigkeit];
}
