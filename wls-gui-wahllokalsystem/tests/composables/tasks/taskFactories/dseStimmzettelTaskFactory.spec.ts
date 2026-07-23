import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { useWahlTestDataFactory } from "@tests/utils/wahl/WahlTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { beforeEach, describe, expect, it, vi } from "vitest";

import { useDSEStimmzettelTaskFactory } from "@/composables/tasks/taskFactories/dseStimmzettelTaskFactory.ts";
import { useWahlenStore } from "@/stores/wahlenStore.ts";

const mockDefinitions = vi.hoisted(() => ({
  getStimmzettel: vi.fn(),
}));

vi.mock(import("@/composables/dse/stimmzettelService.ts"), () => ({
  useStimmzettelService: vi.fn().mockImplementation(() => ({
    getStimmzettel: mockDefinitions.getStimmzettel,
  })),
}));

describe("dseStimmzettelTaskFactory.ts", () => {
  let unitUnderTest: ReturnType<typeof useDSEStimmzettelTaskFactory>;

  const { createTaskFactoryContext } = useTasksTestDataFactory();
  const { prepareWahl } = useWahlTestDataFactory();

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useDSEStimmzettelTaskFactory();
  });

  describe("createTasks", () => {
    it("should_returnTaskList_when_called", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
    });

    it("should_haveExpectedCallback_when_called", () => {
      const taskFactoryContext = createTaskFactoryContext();
      const wahlenStore = useWahlenStore();

      wahlenStore.wahlenState.wahlen = [
        prepareWahl()
          // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
          .wahlID(taskFactoryContext.extendedWahlMetaData[0]!.wahlID)
          .build(),
      ];

      mockDefinitions.getStimmzettel.mockReturnValue(Promise.resolve());

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();
      expect(mockDefinitions.getStimmzettel).toHaveBeenCalledOnce();
      expect(result[0]?.name).toContain("Stimmzettel");
    });
  });
});
