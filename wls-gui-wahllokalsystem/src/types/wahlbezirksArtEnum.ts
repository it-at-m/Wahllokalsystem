export const WahlbezirksArtEnum = {
  UWB: "UWB",
  BWB: "BWB",
} as const;

export type WahlbezirksArtEnum =
  (typeof WahlbezirksArtEnum)[keyof typeof WahlbezirksArtEnum];
