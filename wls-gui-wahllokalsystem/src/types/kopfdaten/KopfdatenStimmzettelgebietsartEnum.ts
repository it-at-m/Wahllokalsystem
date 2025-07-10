import { KopfdatenDTOStimmzettelgebietsartEnum } from "@/api/wls-clients/generated-basisdaten-api";

/**
 * SB = Stadtbezirk
 * SG = Stimmgebiet
 * SK = Stimmkreis
 * WK = Wahlkreis
 */
export const KopfdatenStimmzettelgebietsartEnum = {
  Sb: "SB",
  Sg: "SG",
  Sk: "SK",
  Wk: "WK",
} as const;

export type KopfdatenStimmzettelgebietsartEnum =
  (typeof KopfdatenDTOStimmzettelgebietsartEnum)[keyof typeof KopfdatenStimmzettelgebietsartEnum];
