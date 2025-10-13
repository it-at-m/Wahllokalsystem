import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useBegruendungTaskFactory } from "@/composables/tasks/taskFactories/begruendungTaskFactory.ts";

const mockDefinitions = vi.hoisted(() => ({
  loadBegruendungForWahl: vi.fn(),
}));

vi.mock("@/stores/ergebnismeldungStore.ts", () => ({
  useErgebnismeldungStore: vi.fn().mockImplementation(() => ({
    loadBegruendungForWahl: mockDefinitions.loadBegruendungForWahl,
  })),
}));

describe("begruendungTaskFactory.ts", () => {
  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useBegruendungTaskFactory();

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
      mockDefinitions.loadBegruendungForWahl.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.loadBegruendungForWahl).toHaveBeenCalledOnce();
    });
  });
});
