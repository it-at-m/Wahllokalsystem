export const DseStepsEnum = {
  DSE_STIMMZETTELERFASSUNG: "DSE_STIMMZETTELERFASSUNG",
  DSE_MONITORING: "DSE_MONITORING",
  DSE_BESCHLUSSFASSUNG: "DSE_BESCHLUSSFASSUNG",
} as const;
export type DseStepsEnum = (typeof DseStepsEnum)[keyof typeof DseStepsEnum];
