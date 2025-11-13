export const MeldungValidierungsstatusEnum = {
  NichtValidiert: "NICHT_VALIDIERT",
  NichtGesendet: "NICHT_GESENDET",
  Valide: "VALIDE",
  Invalide: "INVALIDE",
} as const;

export type MeldungValidierungsstatusEnum =
  (typeof MeldungValidierungsstatusEnum)[keyof typeof MeldungValidierungsstatusEnum];
