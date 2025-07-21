import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts";

describe("konfigurationsparameterTaskFactory.ts", () => {
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useKonfigurationsparameterTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_calledWithCorrectParameters", () => {
      const taskFactoryContext: TaskFactoryContext = createTaskFactoryContext();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });
  });
});
