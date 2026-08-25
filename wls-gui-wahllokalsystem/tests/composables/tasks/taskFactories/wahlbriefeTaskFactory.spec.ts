import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahlbriefeTaskFactory } from "@/composables/tasks/taskFactories/wahlbriefeTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initWahlbriefdaten: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: () => ({
    wahlbriefDatenActions: {
      initWahlbriefdaten: mockDefinitions.initWahlbriefdaten,
    },
  }),
}));

describe("wahlbriefeTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useWahlbriefeTaskFactory();

  afterEach(() => {
    vi.clearAllMocks();
  });

  describe("createTasks", () => {
    describe("userHasRoleSchriftfuehrung", () => {
      it("should_createTask_when_WahlbezirkArtIsBWB", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .wahlbezirkArt(WahlbezirksArtEnum.BWB)
          .build();

        mockDefinitions.initWahlbriefdaten.mockReturnValue(Promise.resolve());

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(1);

        result[0]?.callback();

        expect(mockDefinitions.initWahlbriefdaten).toHaveBeenCalledWith(false);
      });

      it("should_returnNoTasks_when_calledWithWrongWahlbezirkArt", () => {
        const taskFactoryContext = prepareTaskFactoryContext()
          .isSchriftfuehrung(true)
          .wahlbezirkArt(WahlbezirksArtEnum.UWB)
          .build();

        const result = createTasks(taskFactoryContext);

        expect(result.length).toStrictEqual(0);
      });
    });
  });

  describe("userHasNotRoleSchriftfuehrung", () => {
    it("should_returnNoTasks_when_called", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .isSchriftfuehrung(false)
        .build();

      const result = createTasks(taskFactoryContext);
      expect(result.length).toStrictEqual(0);
    });
  });
});
