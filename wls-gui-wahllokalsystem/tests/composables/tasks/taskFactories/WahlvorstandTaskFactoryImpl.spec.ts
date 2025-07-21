import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it } from "vitest";

import { WahlvorstandTaskFactoryImpl } from "@/composables/tasks/taskFactories/WahlvorstandTaskFactoryImpl.ts";

describe("WahlvorstandTaskFactoryImpl.ts", () => {
  const { createTaskFactoryData } = useTasksTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_calledWithCorrectParameters", () => {
      const wahlvorstandTaskFactoryImpl = new WahlvorstandTaskFactoryImpl();
      const taskFactoryData: TaskFactoryContext = createTaskFactoryData();

      const result = wahlvorstandTaskFactoryImpl.createTasks(taskFactoryData);

      expect(result.length).toStrictEqual(1);
    });
  });
});
