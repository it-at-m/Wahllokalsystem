import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useEreignisseTaskFactory } from "@/composables/tasks/taskFactories/ereignisseTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadEreignisse: vi.fn(),
}));

vi.mock("@/stores/ereignisStore.ts", () => ({
  useEreignisStore: vi.fn().mockImplementation(() => ({
    loadEreignisse: mockDefinitions.loadEreignisse,
  })),
}));

describe("ereignisseTaskFactory.ts", () => {
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useEreignisseTaskFactory();

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
      mockDefinitions.loadEreignisse.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.loadEreignisse).toHaveBeenCalledOnce();
    });
  });
});
