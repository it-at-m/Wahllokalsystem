import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { createPinia, setActivePinia } from "pinia";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";

import { useBeanstandeteWahlbriefeTaskFactory } from "@/composables/tasks/taskFactories/beanstandeteWahlbriefeTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initBeanstandeteWahlbriefe: vi.fn(),
}));

vi.mock("@/stores/wahlenStore.ts", () => ({
  useWahlenStore: vi.fn().mockImplementation(() => ({
    beanstandeteWahlbriefeActions: vi.fn().mockImplementation(() => ({
      initBeanstandeteWahlbriefe: mockDefinitions.initBeanstandeteWahlbriefe,
    })),
  })),
}));

describe("beanstandeteWahlbriefeTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();

  let unitUnderTest: ReturnType<typeof useBeanstandeteWahlbriefeTaskFactory>;

  beforeEach(() => {
    setActivePinia(createPinia());
    unitUnderTest = useBeanstandeteWahlbriefeTaskFactory();
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("createTasks", () => {
    it("should_returnTaskListWithTask_when_wahlbezirkIsBWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);
      // eslint-disable-next-line  @typescript-eslint/no-non-null-assertion
      expect(result[0]!.name).toStrictEqual("Zugelassene Wahlbriefe");
    });

    it("should_returnEmptyList_when_wahlbezirkIsUWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      const result = unitUnderTest.createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(0);
    });
  });
});