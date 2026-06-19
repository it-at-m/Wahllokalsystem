import type { Task } from "@/types/tasks/Task.ts";

import { createTestingPinia } from "@pinia/testing";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useInitTaskManagerStore } from "@/stores/initTaskManagerStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  initTasklist: vi.fn(),
}));

vi.mock(import("@/composables/tasks/taskListService.ts"), () => ({
  useTaskListService: () => ({
    initTasklist: mockDefinitions.initTasklist,
  }),
}));

describe("initTaskManagerStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useInitTaskManagerStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useInitTaskManagerStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("initTasks", () => {
    it("should_setTaskSuccessful_when_noErrorsWhileRunningTask", async () => {
      const exampleTaskList: Task[] = [
        {
          name: "Test",
          callback: () => {
            return Promise.resolve();
          },
        },
      ];
      mockDefinitions.initTasklist.mockReturnValue(exampleTaskList);

      await unitUnderTest.initTasks();

      expect(unitUnderTest.successfullyTasks.length).toStrictEqual(1);
      expect(unitUnderTest.failedTasks.length).toStrictEqual(0);
      expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
      expect(unitUnderTest.successfullyTasks).contains(exampleTaskList[0]);
    });

    it("should_setTaskFailed_when_errorWhileRunningTask", async () => {
      const exampleTaskList: Task[] = [
        {
          name: "Wahlen",
          callback: () => {
            return Promise.reject();
          },
        },
      ];
      mockDefinitions.initTasklist.mockReturnValue(exampleTaskList);

      await unitUnderTest.initTasks();

      expect(unitUnderTest.successfullyTasks.length).toStrictEqual(0);
      expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
      expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
      expect(unitUnderTest.failedTasks).contains(exampleTaskList[0]);
    });

    it("should_setTaskInCorrectList_when_givenMultipleTasks", async () => {
      const exampleTaskList: Task[] = [
        {
          name: "Test",
          callback: () => {
            return Promise.resolve();
          },
        },
        {
          name: "Test2",
          callback: () => {
            return Promise.reject();
          },
        },
      ];
      mockDefinitions.initTasklist.mockReturnValue(exampleTaskList);

      await unitUnderTest.initTasks();

      expect(unitUnderTest.successfullyTasks.length).toStrictEqual(1);
      expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
      expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(2);
      expect(unitUnderTest.successfullyTasks).contains(exampleTaskList[0]);
      expect(unitUnderTest.failedTasks).contains(exampleTaskList[1]);
    });
  });
});
