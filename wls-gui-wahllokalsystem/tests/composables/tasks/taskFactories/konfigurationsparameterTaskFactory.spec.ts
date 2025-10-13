import type { TaskFactoryContext } from "@/composables/tasks/TaskFactoryContext.ts";

import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useKonfigurationsparameterTaskFactory } from "@/composables/tasks/taskFactories/konfigurationsparameterTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  initKonfigurationsparameter: vi.fn(),
}));

vi.mock("@/stores/infomanagementStore.ts", () => ({
  useInfomanagementStore: vi.fn().mockImplementation(() => ({
    initKonfigurationsparameter: mockDefinitions.initKonfigurationsparameter,
  })),
}));

describe("konfigurationsparameterTaskFactory.ts", () => {
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useKonfigurationsparameterTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_calledIndependentlyOfParameters", () => {
      const taskFactoryContext: TaskFactoryContext = createTaskFactoryContext();

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });

    it("should_haveExpectedCallback_when_calledIndependentlyOfParameters", () => {
      const taskFactoryContext: TaskFactoryContext = createTaskFactoryContext();
      mockDefinitions.initKonfigurationsparameter.mockReturnValue(
        Promise.resolve
      );

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();

      expect(
        mockDefinitions.initKonfigurationsparameter
      ).toHaveBeenCalledOnce();
    });
  });
});
