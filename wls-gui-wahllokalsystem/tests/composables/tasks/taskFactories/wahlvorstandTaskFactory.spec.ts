import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useWahlvorstandTaskFactory } from "@/composables/tasks/taskFactories/wahlvorstandTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  initWahlvorstand: vi.fn(),
}));

vi.mock("@/stores/wahlvorstandStore.ts", () => ({
  useWahlvorstandStore: vi.fn().mockImplementation(() => ({
    initWahlvorstand: mockDefinitions.initWahlvorstand,
  })),
}));

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

    it("should_haveExpectedCallback_when_calledIndependentlyOfContext", () => {
      const taskFactoryContext = createTaskFactoryContext();
      mockDefinitions.initWahlvorstand.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.initWahlvorstand).toHaveBeenCalledOnce();
    });
  });
});
