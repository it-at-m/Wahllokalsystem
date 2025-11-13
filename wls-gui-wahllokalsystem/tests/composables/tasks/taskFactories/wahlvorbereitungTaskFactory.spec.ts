import { useTasksTestDataFactory } from "@tests/utils/tasks/TasksTestDataFactory.ts";
import { afterEach, describe, expect, it, vi } from "vitest";

import { useWahlvorbereitungTaskFactory } from "@/composables/tasks/taskFactories/wahlvorbereitungTaskFactory.ts";
import { WahlbezirksArtEnum } from "@/types/wahlbezirksArtEnum.ts";

const mockDefinitions = vi.hoisted(() => ({
  initUrnenwahlvorbereitung: vi.fn(),
  initBriefwahlvorbereitung: vi.fn(),
}));

vi.mock("@/stores/wahlbezirkStore.ts", () => ({
  useWahlbezirkStore: () => ({
    urnenwahlVorbereitungActions: {
      initUrnenwahlvorbereitung: mockDefinitions.initUrnenwahlvorbereitung,
    },
    briefwahlVorbereitungActions: {
      initBriefwahlvorbereitung: mockDefinitions.initBriefwahlvorbereitung,
    },
  }),
}));

describe("wahlvorbereitungTaskFactory.ts", () => {
  const { prepareTaskFactoryContext } = useTasksTestDataFactory();
  const { createTasks } = useWahlvorbereitungTaskFactory();

  afterEach(() => {
    vi.clearAllMocks();
  });
  describe("createTasks", () => {
    it("should_createTaskWithInitUrnenwahlvorbereitung_when_WahlbezirkArtIsUWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.UWB)
        .build();

      mockDefinitions.initUrnenwahlvorbereitung.mockReturnValue(
        Promise.resolve()
      );

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();

      expect(mockDefinitions.initUrnenwahlvorbereitung).toHaveBeenCalledWith(
        false
      );
    });

    it("should_createTaskWithInitBriefwahlvorbereitung_when_WahlbezirkArtIsBWB", () => {
      const taskFactoryContext = prepareTaskFactoryContext()
        .wahlbezirkArt(WahlbezirksArtEnum.BWB)
        .build();

      mockDefinitions.initBriefwahlvorbereitung.mockReturnValue(
        Promise.resolve()
      );

      const result = createTasks(taskFactoryContext);

      expect(result.length).toStrictEqual(1);

      result[0]?.callback();

      expect(mockDefinitions.initBriefwahlvorbereitung).toHaveBeenCalledWith(
        false
      );
    });
  });
});
