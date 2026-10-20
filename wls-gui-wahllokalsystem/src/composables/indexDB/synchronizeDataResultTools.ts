import type { SyncronizeDataResult } from "@/types/indexDB/SyncronizeDataResult.ts";

export function useSynchronizeDataResultTools() {
  function hasRunAndSuccessfullySynchronizedAllDirtyData(
    result: SyncronizeDataResult | null
  ) {
    if (!result) {
      return false;
    }

    if (
      result.numberOfTasksFailed > 0 ||
      result.numberOfDirtyTasksRemaining > 0
    ) {
      return false;
    }

    return true;
  }

  return {
    hasRunAndSuccessfullySynchronizedAllDirtyData,
  };
}
