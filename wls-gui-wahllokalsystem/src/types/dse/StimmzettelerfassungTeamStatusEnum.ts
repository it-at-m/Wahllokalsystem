export const StimmzettelerfassungTeamStatusEnum = {
  REGISTRIERT: "REGISTRIERT",
  IN_BEARBEITUNG: "IN_BEARBEITUNG",
  UNTERBROCHEN: "UNTERBROCHEN",
  ABGESCHLOSSEN: "ABGESCHLOSSEN"
} as const;

export type StimmzettelerfassungTeamStatusEnum =
    (typeof StimmzettelerfassungTeamStatusEnum)[keyof typeof StimmzettelerfassungTeamStatusEnum];
