import { KopfdatenDTOStimmzettelgebietsartEnum } from "@/api/wls-clients/generated-basisdaten-api";

export const KopfdatenStimmzettelgebietsartEnum = {
  Sb: "SB",
  Sg: "SG",
  Sk: "SK",
  Wk: "WK",
} as const;

export type KopfdatenStimmzettelgebietsartEnum =
  (typeof KopfdatenDTOStimmzettelgebietsartEnum)[keyof typeof KopfdatenStimmzettelgebietsartEnum];
