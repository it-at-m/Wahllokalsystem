import { useSynchronizeDataResultTestDataFactory } from "@tests/utils/indexDB/SynchronizeDataResultTestDataFactory.ts";
import { describe, expect, it } from "vitest";

import { useSynchronizeDataResultTools } from "@/composables/indexDB/synchronizeDataResultTools.ts";

describe("synchronizeDataResultTools.ts", () => {
  const { prepareSynchronizeDataResult } =
    useSynchronizeDataResultTestDataFactory();
  const { hasRunAndSuccessfullySynchronizedAllDirtyData } =
    useSynchronizeDataResultTools();

  describe("hasRunAndSuccessfullySynchronizedAllDirtyData", () => {
    it("should_returnFalse_when_noSynchronizationHasRun", () => {
      expect(hasRunAndSuccessfullySynchronizedAllDirtyData(null)).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_synchronizationContainsFailedTasks", () => {
      const result = prepareSynchronizeDataResult()
        .numberOfTasksFailed(1)
        .build();

      expect(
        hasRunAndSuccessfullySynchronizedAllDirtyData(result)
      ).toStrictEqual(false);
    });

    it("should_returnFalse_when_dirtyTasksRemainAfterSynchronization", () => {
      const result = prepareSynchronizeDataResult()
        .numberOfDirtyTasksRemaining(1)
        .build();

      expect(
        hasRunAndSuccessfullySynchronizedAllDirtyData(result)
      ).toStrictEqual(false);
    });

    it("should_returnTrue_when_synchronizationCompletedWithoutFailedOrRemainingDirtyTasks", () => {
      const result = prepareSynchronizeDataResult()
        .numberOfTasksRan(0)
        .numberOfTasksFailed(0)
        .numberOfTasksSucceeded(0)
        .numberOfDirtyTasksRemaining(0)
        .build();

      expect(
        hasRunAndSuccessfullySynchronizedAllDirtyData(result)
      ).toStrictEqual(true);
    });
  });
});
