export const ErfassungTeamStatusEnum = {
  REGISTRIERT: "REGISTRIERT",
  IN_BEARBEITUNG: "IN_BEARBEITUNG",
  UNTERBROCHEN: "UNTERBROCHEN",
  ABGESCHLOSSEN: "ABGESCHLOSSEN"
} as const;

export type ErfassungTeamStatusEnum =
    (typeof ErfassungTeamStatusEnum)[keyof typeof ErfassungTeamStatusEnum];
