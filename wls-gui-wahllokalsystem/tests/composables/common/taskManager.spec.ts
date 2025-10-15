import type { Task } from "@/types/tasks/Task.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { flushPromises } from "@vue/test-utils";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import { nextTick } from "vue";

import { useTaskManager } from "@/composables/tasks/taskManager.ts";

const { createTask, prepareTask } = useTasksTestDataFactory();

describe("taskManager.ts", () => {
  let unitUnderTest: ReturnType<typeof useTaskManager>;

  beforeEach(() => {
    unitUnderTest = useTaskManager();
    vi.useFakeTimers();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  describe("failedTasks", () => {
    it("should_getEntry_when_aTaskFailsWhileExecution", async () => {
      const taskThatFails1: Task = {
        name: "failing task1",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatFails2: Task = {
        name: "failing task2",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatSucceeded1: Task = {
        name: "successful task1",
        callback: vi.fn().mockResolvedValue(null),
      };
      const taskThatSucceeded2: Task = {
        name: "successful task2",
        callback: vi.fn().mockResolvedValue(null),
      };
      unitUnderTest.setTasks([
        taskThatFails1,
        taskThatFails2,
        taskThatSucceeded1,
        taskThatSucceeded2,
      ]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.failedTasks.value).toStrictEqual([
        taskThatFails1,
        taskThatFails2,
      ]);
    });
  });

  describe("hasAllTasksRun", () => {
    it("should_returnTrue_when_noTasksAreSet", () => {
      unitUnderTest.setTasks([]);
      expect(unitUnderTest.hasAllTasksRun.value).toStrictEqual(true);
    });

    it("should_returnFalse_when_tasksAreSetButHasNotRunYet", () => {
      unitUnderTest.setTasks([{ name: "mocked task", callback: vi.fn() }]);
      expect(unitUnderTest.hasAllTasksRun.value).toStrictEqual(false);
    });

    it("should_returnFalse_when_isRunningButNotAllTasksAreDone", async () => {
      const timeout = 100;

      const task1: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              resolve({});
            }, timeout);
          })
        ),
      };
      const task2: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              resolve({});
            }, timeout);
          })
        ),
      };
      unitUnderTest.setTasks([task1, task2]);

      const runAllTasksPromise = unitUnderTest.runAllTasks();

      expect(unitUnderTest.hasAllTasksRun.value).toStrictEqual(false);

      vi.advanceTimersByTime(timeout * 2);
      await runAllTasksPromise;
    });

    it("should_returnTrue_when_allTasksHaveRun", async () => {
      const taskThatFails: Task = {
        name: "failing task",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatSucceeded: Task = {
        name: "successful task",
        callback: vi.fn().mockResolvedValue(null),
      };
      unitUnderTest.setTasks([taskThatFails, taskThatSucceeded]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.hasAllTasksRun.value).toStrictEqual(true);
    });
  });

  describe("hasAllTasksRunSuccessfully", () => {
    it("should_returnTrue_when_noTasksAreSet", () => {
      unitUnderTest.setTasks([]);
      expect(unitUnderTest.hasAllTasksRunSuccessfully.value).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_tasksAreSetButHasNotRunYet", () => {
      unitUnderTest.setTasks([{ name: "mocked task", callback: vi.fn() }]);
      expect(unitUnderTest.hasAllTasksRunSuccessfully.value).toStrictEqual(
        false
      );
    });

    it("should_returnFalse_when_isRunningButNotAllTasksAreDone", async () => {
      const timeout = 100;

      const task1: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              resolve({});
            }, timeout);
          })
        ),
      };
      const task2: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              resolve({});
            }, timeout);
          })
        ),
      };
      unitUnderTest.setTasks([task1, task2]);

      const runAllTasksPromise = unitUnderTest.runAllTasks();

      expect(unitUnderTest.hasAllTasksRunSuccessfully.value).toStrictEqual(
        false
      );

      vi.advanceTimersByTime(timeout * 2);
      await runAllTasksPromise;
    });

    it("should_returnTrue_when_allTasksHaveRunWithOutError", async () => {
      const taskThatFails: Task = {
        name: "failing task",
        callback: vi.fn().mockResolvedValue(null),
      };
      const taskThatSucceeded: Task = {
        name: "successful task",
        callback: vi.fn().mockResolvedValue(null),
      };
      unitUnderTest.setTasks([taskThatFails, taskThatSucceeded]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.hasAllTasksRunSuccessfully.value).toStrictEqual(
        true
      );
    });

    it("should_returnFalse_when_allTasksHaveRunButAtLeastOneProducedAnError", async () => {
      const taskThatFails: Task = {
        name: "failing task",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatSucceeded: Task = {
        name: "successful task",
        callback: vi.fn().mockResolvedValue(null),
      };
      unitUnderTest.setTasks([taskThatFails, taskThatSucceeded]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.hasAllTasksRunSuccessfully.value).toStrictEqual(
        false
      );
    });
  });

  describe("hasTasksToRun", () => {
    it("should_returnTrue_when_tasksAreSet", () => {
      unitUnderTest.setTasks([{ name: "mocked task", callback: vi.fn() }]);
      expect(unitUnderTest.hasTasksToRun.value).toStrictEqual(true);
    });

    it("should_returnFalse_when_noTasksAreSet", () => {
      unitUnderTest.setTasks([]);
      expect(unitUnderTest.hasTasksToRun.value).toStrictEqual(false);
    });

    it("should_returnFalse_when_composableIsInitializedWithoutArguments", () => {
      unitUnderTest = useTaskManager();
      expect(unitUnderTest.hasTasksToRun.value).toStrictEqual(false);
    });

    it("should_returnTrue_when_composableIsInitializedWithTasks", () => {
      unitUnderTest = useTaskManager([
        { name: "mocked task", callback: vi.fn() },
      ]);
      expect(unitUnderTest.hasTasksToRun.value).toStrictEqual(true);
    });

    it("should_returnFalse_when_composableIsInitializedWithoutTasks", () => {
      unitUnderTest = useTaskManager([]);
      expect(unitUnderTest.hasTasksToRun.value).toStrictEqual(false);
    });
  });

  describe("successfullyTasks", () => {
    it("should_getEntry_when_aTaskSucceededWhileExecution", async () => {
      const taskThatFails1: Task = {
        name: "failing task1",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatFails2: Task = {
        name: "failing task2",
        callback: vi.fn().mockRejectedValue(new Error("mocked task failed")),
      };
      const taskThatSucceeded1: Task = {
        name: "successful task1",
        callback: vi.fn().mockResolvedValue(null),
      };
      const taskThatSucceeded2: Task = {
        name: "successful task2",
        callback: vi.fn().mockResolvedValue(null),
      };
      unitUnderTest.setTasks([
        taskThatFails1,
        taskThatFails2,
        taskThatSucceeded1,
        taskThatSucceeded2,
      ]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.successfullyTasks.value).toStrictEqual([
        taskThatSucceeded1,
        taskThatSucceeded2,
      ]);
    });
  });

  describe("numberOfTasksFinished", () => {
    it("should_returnNumberEqualAllTasks_when_allTasksHaveRun", async () => {
      const task1 = {
        name: "task1",
        callback: vi.fn(),
      };
      const task2 = {
        name: "task2",
        callback: vi.fn(),
      };
      unitUnderTest.setTasks([task1, task2]);

      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(0);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(2);
    });

    it("should_update_when_tasksAreRunning", async () => {
      const timeout = 100;

      const task1: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              console.debug("task1 finishes");
              resolve({});
            }, timeout);
          })
        ),
      };
      const task2: Task = {
        name: "successful task",
        callback: vi.fn().mockReturnValue(
          new Promise((resolve) => {
            setTimeout(() => {
              console.debug("task2 finishes");
              resolve({});
            }, timeout);
          })
        ),
      };
      unitUnderTest.setTasks([task1, task2]);

      const runAllTasksPromise = unitUnderTest.runAllTasks();

      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(0);

      vi.advanceTimersByTime(timeout);
      await nextTick(); //wait to reevaluate computed props
      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(1);

      vi.advanceTimersByTime(timeout);
      await flushPromises(); //wait to reevaluate computed props
      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(2);

      await runAllTasksPromise;
      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(2);
    });

    it("should_returnSumOfNumbersOfSuccessfulAndFailedTasks", () => {
      unitUnderTest.successfullyTasks.value = [
        createTask("task1"),
        createTask("task2"),
      ];
      unitUnderTest.failedTasks.value = [
        createTask("task3"),
        createTask("task4"),
        createTask("task5"),
      ];

      expect(unitUnderTest.numberOfTasksFinished.value).toStrictEqual(5);
    });
  });

  describe("numberOfTasksToRun", () => {
    it("should_return0_when_noTasksWhereSet", () => {
      unitUnderTest.setTasks([]);
      expect(unitUnderTest.numberOfTasksToRun.value).toStrictEqual(0);
    });

    it("should_returnNumberOfTasks_when_tasksWhereSet", () => {
      unitUnderTest.setTasks([
        createTask("task1"),
        createTask("task2"),
        createTask("task3"),
      ]);
      expect(unitUnderTest.numberOfTasksToRun.value).toStrictEqual(3);
    });

    it("should_return0_when_initializedWithoutTasks", () => {
      unitUnderTest = useTaskManager([]);
      expect(unitUnderTest.numberOfTasksToRun.value).toStrictEqual(0);
    });

    it("should_returnNumberOfTasks_when_initializedWithTasks", () => {
      unitUnderTest = useTaskManager([
        createTask("task1"),
        createTask("task2"),
        createTask("task3"),
      ]);
      expect(unitUnderTest.numberOfTasksToRun.value).toStrictEqual(3);
    });

    it("should_return0_when_initializedWithoutArguments", () => {
      unitUnderTest = useTaskManager();
      expect(unitUnderTest.numberOfTasksToRun.value).toStrictEqual(0);
    });
  });

  describe("runAllTasks", () => {
    it("should_runAll_when_tasksAreSet", async () => {
      const task1 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      const task2 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      const task3 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      unitUnderTest.setTasks([task1, task2, task3]);

      await unitUnderTest.runAllTasks();

      expect(task1.callback).toHaveBeenCalled();
      expect(task2.callback).toHaveBeenCalled();
      expect(task3.callback).toHaveBeenCalled();
    });

    it("should_runAllTasksEven_when_someTasksThrowError", async () => {
      const task1 = prepareTask()
        .callback(vi.fn().mockRejectedValue(new Error()))
        .build();
      const task2 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      const task3 = prepareTask()
        .callback(vi.fn().mockRejectedValue(new Error()))
        .build();
      unitUnderTest.setTasks([task1, task2, task3]);

      await unitUnderTest.runAllTasks();

      expect(unitUnderTest.successfullyTasks.value).toStrictEqual([task2]);
      expect(unitUnderTest.failedTasks.value).toStrictEqual([task1, task3]);
    });
  });

  describe("rerunFailedTasks", () => {
    it("should_runFailedTasks_when_failedTasksAreSet", async () => {
      const task1 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      const task2 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      const task3 = prepareTask()
        .callback(vi.fn().mockResolvedValue(null))
        .build();
      unitUnderTest.successfullyTasks.value = [task1];
      unitUnderTest.failedTasks.value = [task2, task3];

      await unitUnderTest.rerunFailedTasks();

      expect(task1.callback).not.toHaveBeenCalled();
      expect(task2.callback).toHaveBeenCalled();
      expect(task3.callback).toHaveBeenCalled();
    });
  });
});
