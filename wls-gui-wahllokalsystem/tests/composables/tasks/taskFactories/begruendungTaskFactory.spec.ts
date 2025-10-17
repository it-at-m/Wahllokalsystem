import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useBegruendungTaskFactory } from "@/composables/tasks/taskFactories/begruendungTaskFactory.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

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
  const { prepareWahl } = useWahlTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_calledIndependentlyOfContext", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });

    it("should_haveExpectedCallback_when_calledIndependentlyOfContext", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      mockDefinitions.loadBegruendungForWahl.mockReturnValue(Promise.resolve());

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.loadBegruendungForWahl).toHaveBeenCalledOnce();
    });
  });
});
