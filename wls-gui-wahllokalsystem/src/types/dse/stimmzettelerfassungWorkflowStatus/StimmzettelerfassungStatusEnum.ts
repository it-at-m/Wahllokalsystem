export const StimmzettelerfassungStatusEnum = {
  SteBearbeitung: "STE_BEARBEITUNG",
  SteAbgeschlossen: "STE_ABGESCHLOSSEN",
  BeAbgeschlossen: "BE_ABGESCHLOSSEN",
} as const;

export type StimmzettelerfassungStatusEnum =
  (typeof StimmzettelerfassungStatusEnum)[keyof typeof StimmzettelerfassungStatusEnum];
