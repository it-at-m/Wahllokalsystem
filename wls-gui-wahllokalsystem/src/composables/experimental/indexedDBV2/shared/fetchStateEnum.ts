export const FetchStateEnum = {
  PENDING: "PENDING",
  DONE: "DONE",
  ERROR: "ERROR",
} as const;
export type FetchStateEnum =
  (typeof FetchStateEnum)[keyof typeof FetchStateEnum];

export function isTransfered(fetchState: FetchStateEnum) {
  return fetchState === FetchStateEnum.DONE;
}
