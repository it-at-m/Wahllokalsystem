export const KandidatEventTypeEnum = {
  ADD_VOTE: "ADD_VOTE",
  SET_VOTE: "SET_VOTE",
  DISCARD: "DISCARD",
} as const;

export type KandidatEventTypeEnum =
  (typeof KandidatEventTypeEnum)[keyof typeof KandidatEventTypeEnum];
