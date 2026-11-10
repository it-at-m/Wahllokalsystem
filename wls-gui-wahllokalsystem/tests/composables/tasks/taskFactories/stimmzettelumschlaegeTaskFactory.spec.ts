import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { afterAll, afterEach, describe, expect, it, vi } from "vitest";

import { useUrnenwahlSchliessungsuhrzeitTaskFactory } from "@/composables/tasks/taskFactories/urnenwahlSchliessungsuhrzeitTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initSchliessungsuhrzeit: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: () => ({
    schliessungsuhrzeitActions: {
      initSchliessungsuhrzeit: mockDefinitions.initSchliessungsuhrzeit,
    },
  }),
}));

describe("urnenwahlSchliessungsuhrzeitTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useUrnenwahlSchliessungsuhrzeitTaskFactory();

  afterEach(() => {
    vi.clearAllMocks();
  });

  afterAll(() => {
    vi.resetAllMocks();
  });

  describe("createTasks", () => {
    it("should_returnOneTask_when_wahlbezirkArtIsUWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();
      const result = createTasks(taskFactoryContext);

      result[0]?.callback();

      expect(result.length).toBe(1);
      expect(mockDefinitions.initSchliessungsuhrzeit).toHaveBeenCalledOnce();
    });

    it("should_returnNoTasks_when_wahlbezirkArtIsBWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();
      const result = createTasks(taskFactoryContext);

      expect(result.length).toBe(0);
    });
  });
});
