export const WahlvorschlagEventTypeEnum = {
  SELECT: "SELECT",
} as const;

export type WahlvorschlagEventTypeEnum =
  (typeof WahlvorschlagEventTypeEnum)[keyof typeof WahlvorschlagEventTypeEnum];
