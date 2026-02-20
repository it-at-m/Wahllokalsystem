export const WahlWahlartEnum = {
  Baw: "BAW",
  Beb: "BEB",
  Btw: "BTW",
  Bzw: "BZW",
  Euw: "EUW",
  Ltw: "LTW",
  Mbw: "MBW",
  Obw: "OBW",
  Srw: "SRW",
  Svw: "SVW",
  Ve: "VE",
} as const;

export type WahlWahlartEnum =
  (typeof WahlWahlartEnum)[keyof typeof WahlWahlartEnum];
