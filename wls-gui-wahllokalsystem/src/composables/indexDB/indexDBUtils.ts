import type { IndexDBValue } from "@/types/indexDB/IndexDBValue.ts";

export function useIndexDBUtils() {
  function compareByTimestamp(a: IndexDBValue, b: IndexDBValue) {
    if (a.timestamp === undefined && b.timestamp === undefined) {
      return 0;
    }

    if (a.timestamp === undefined) {
      return 1;
    }

    if (b.timestamp === undefined) {
      return -1;
    }

    return a.timestamp - b.timestamp;
  }

  return { compareByTimestamp };
}
