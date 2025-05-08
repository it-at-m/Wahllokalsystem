import type { Task } from "@/types/Task.ts";

import { createTestingPinia } from "@pinia/testing";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/WahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  getTaskList: vi.fn(),
}));

vi.mock("@/composables/tasks/taskListService", () => ({
  useTaskListService: () => ({
    getTaskList: mockDefinitions.getTaskList,
  }),
}));

describe("taskListService.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskManagerStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useTaskManagerStore(testPinia);
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it("should_setTaskSuccessful_when_noErrorsWhileRunningTask", async () => {
    const exampleTaskList: Task[] = [
      {
        name: "Test",
        wahlbezirksart: undefined,
        onlyForWahlen: [WahlWahlartEnum.Obw, WahlWahlartEnum.Bzw],
        onlyForAllWVZs: undefined,
        callback: () => {
          return Promise.resolve();
        },
      },
    ];
    mockDefinitions.getTaskList.mockReturnValue(exampleTaskList);

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
        wahlbezirksart: undefined,
        onlyForWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        onlyForAllWVZs: undefined,
        callback: () => {
          return Promise.reject();
        },
      },
    ];
    mockDefinitions.getTaskList.mockReturnValue(exampleTaskList);

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(0);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
  });

  it("should_setTaskInCorrectList_when_noGivenMultipleTasks", async () => {
    const exampleTaskList: Task[] = [
      {
        name: "Test",
        wahlbezirksart: undefined,
        onlyForWahlen: [WahlWahlartEnum.Obw],
        onlyForAllWVZs: undefined,
        callback: () => {
          return Promise.resolve();
        },
      },
      {
        name: "Test2",
        wahlbezirksart: undefined,
        onlyForWahlen: [WahlWahlartEnum.Obw, WahlWahlartEnum.Bzw],
        onlyForAllWVZs: undefined,
        callback: () => {
          return Promise.reject();
        },
      },
    ];
    mockDefinitions.getTaskList.mockReturnValue(exampleTaskList);

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(1);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(2);
    expect(unitUnderTest.successfullyTasks).contains(exampleTaskList[0]);
    expect(unitUnderTest.failedTasks).contains(exampleTaskList[1]);
  });
});
