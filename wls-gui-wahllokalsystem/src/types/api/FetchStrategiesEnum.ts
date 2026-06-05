export const FetchStrategiesEnum = {
  STRATEGY_OFFLINE_FIRST: "STRATEGY_OFFLINE_FIRST",
  STRATEGY_ONLINE_ONLY: "STRATEGY_ONLINE_ONLY",
  STRATEGY_POST_ONLINE_ONLY_BUT_DIRTY_ON_FAIL:
    "STRATEGY_POST_ONLINE_ONLY_BUT_DIRTY_ON_FAIL",
  STRATEGY_ONLINE_FIRST: "STRATEGY_ONLINE_FIRST",
} as const;

export type FetchStrategiesEnum =
  (typeof FetchStrategiesEnum)[keyof typeof FetchStrategiesEnum];
