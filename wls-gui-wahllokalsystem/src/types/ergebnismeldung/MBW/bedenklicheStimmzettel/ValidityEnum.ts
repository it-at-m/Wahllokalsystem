export const ValidityEnum = {
  VALID: "VALID",
  PARTIAL_VALID: "PARTIAL_VALID",
  INVALID: "INVALID",
} as const;
export type ValidityEnum = (typeof ValidityEnum)[keyof typeof ValidityEnum];
