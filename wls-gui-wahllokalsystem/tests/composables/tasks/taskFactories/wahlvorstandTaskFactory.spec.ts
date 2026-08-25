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
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useWahlvorstandTaskFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_returnTaskList_when_calledIndependentlyOfContext", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);
      });

      it("should_haveExpectedCallback_when_calledIndependentlyOfContext", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .build();
        mockDefinitions.initWahlvorstand.mockReturnValue(Promise.resolve());

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);

        result[0]?.callback();
        expect(mockDefinitions.initWahlvorstand).toHaveBeenCalledOnce();
      });
    });
  });

  describe("userHasNotRoleSchriftfuehrung", () => {
    it("should_returnEmptyList_when_called", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .build();

      const result = createTasks(taskFactoryContext);
      expect(result.length).toStrictEqual(0);
    });
  });
});
