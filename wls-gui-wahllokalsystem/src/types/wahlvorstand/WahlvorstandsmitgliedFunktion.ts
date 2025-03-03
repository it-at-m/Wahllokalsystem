export const WahlvorstandsmitgliedFunktionEnum = {
  W: "W",
  Sb: "SB",
  Swb: "SWB",
  Ssb: "SSB",
  B: "B",
} as const;

export type WahlvorstandsmitgliedFunktionEnum =
  (typeof WahlvorstandsmitgliedFunktionEnum)[keyof typeof WahlvorstandsmitgliedFunktionEnum];

export function isSchriftfuehrer(
  funktion?: WahlvorstandsmitgliedFunktionEnum
): boolean {
  return (
    funktion === WahlvorstandsmitgliedFunktionEnum.Sb ||
    funktion === WahlvorstandsmitgliedFunktionEnum.Ssb
  );
}

export function isWahlvorsteher(
  funktion?: WahlvorstandsmitgliedFunktionEnum
): boolean {
  return (
    funktion === WahlvorstandsmitgliedFunktionEnum.W ||
    funktion === WahlvorstandsmitgliedFunktionEnum.Swb
  );
}
