import type { Task } from "@/types/Task.ts";

import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useTaskManagerStore } from "@/stores/taskManagerStore.ts";
import { useWahlStore } from "@/stores/wahlStore.ts";
import { WahlWahlartEnum } from "@/types/wahl/wahlWahlartEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadWahlen: vi.fn(),
}));

vi.mock("@/stores/wahlStore.ts", () => ({
  useWahlStore: () => ({
    loadWahlen: mockDefinitions.loadWahlen,
  }),
}));

describe("taskManagerStore.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskManagerStore>;
  let wahlStore: ReturnType<typeof useWahlStore>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useTaskManagerStore();
    wahlStore = useWahlStore();
  });

  it("should_setTaskSuccessful_when_noErrorsWhileRunningTask", async () => {
    const exampleTaskList: Task[] = [
      {
        name: "Wahlen",
        wahlbezirksart: undefined,
        forWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        forAllWVZs: undefined,
        callback: () => {
          return Promise.resolve();
        },
      },
    ];
    unitUnderTest.taskList = exampleTaskList;

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(1);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(0);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
  });

  it("should_setTaskFailed_when_errorWhileRunningTask", async () => {
    const exampleTaskList: Task[] = [
      {
        name: "Wahlen",
        wahlbezirksart: undefined,
        forWahlen: [
          WahlWahlartEnum.Obw,
          WahlWahlartEnum.Bzw,
          WahlWahlartEnum.Srw,
        ],
        forAllWVZs: undefined,
        callback: () => {
          return wahlStore.loadWahlen();
        },
      },
    ];
    unitUnderTest.taskList = exampleTaskList;
    mockDefinitions.loadWahlen.mockRejectedValue(
      new Error("Failed to load Wahlen")
    );

    await unitUnderTest.initTasks();

    expect(unitUnderTest.successfullyTasks.length).toStrictEqual(0);
    expect(unitUnderTest.failedTasks.length).toStrictEqual(1);
    expect(unitUnderTest.numberOfTasksToRun).toStrictEqual(1);
  });
});
