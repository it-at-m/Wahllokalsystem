import type { Task } from "@/types/Task.ts";

import { createTestingPinia } from "@pinia/testing";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

describe("taskManagerStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskManagerStore>;

  beforeEach(() => {
    const testPinia = createTestingPinia({
      stubActions: false,
      createSpy: vi.fn,
    });
    unitUnderTest = useTaskManagerStore(testPinia);
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
    console.log(unitUnderTest.taskList);
    unitUnderTest.taskList = exampleTaskList;
    console.log(unitUnderTest.taskList);

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(1);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(0);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
    expect(unitUnderTest.successfullyTasks).contains(exampleTaskList);
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
    unitUnderTest.taskList = exampleTaskList;

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(0);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
  });
});
