export const WahlvorschlagEventTypeEnum = {
  SELECT: "SELECT",
  DESELECT: "DESELECT",
} as const;

export type WahlvorschlagEventTypeEnum =
  (typeof WahlvorschlagEventTypeEnum)[keyof typeof WahlvorschlagEventTypeEnum];
