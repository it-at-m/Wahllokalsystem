import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts";

describe("wahlvorstandTaskFactory.ts", () => {
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useWahlvorstandTaskFactory();
  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_calledIndependentlyOfContext", () => {
      const taskFactoryContext = createTaskFactoryContext();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });
  });
});
