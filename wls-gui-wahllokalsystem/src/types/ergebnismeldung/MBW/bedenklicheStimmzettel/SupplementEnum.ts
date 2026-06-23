export const SupplementEnum = {
  TOO_MANY_LISTENKREUZE: "TOO_MANY_LISTENKREUZE",
  TOO_MANY_SINGLE_KANDIDAT_VOTES: "TOO_MANY_SINGLE_KANDIDAT_VOTES",
} as const;
export type SupplementEnum =
  (typeof SupplementEnum)[keyof typeof SupplementEnum];
